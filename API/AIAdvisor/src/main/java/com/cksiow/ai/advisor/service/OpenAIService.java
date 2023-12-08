package com.cksiow.ai.advisor.service;


import com.cksiow.ai.advisor.assistant.dto.OpenAIAssistantResponse;
import com.cksiow.ai.advisor.assistant.model.AssistantResponse;
import com.cksiow.ai.advisor.assistant.service.AssistantService;
import com.cksiow.ai.advisor.constant.Constant;
import com.cksiow.ai.advisor.dto.HttpResult;
import com.cksiow.ai.advisor.dto.MessageRequest;
import com.cksiow.ai.advisor.dto.MessageResponse;
import com.cksiow.ai.advisor.thread.model.ThreadCreate;
import com.cksiow.ai.advisor.thread.service.ThreadService;
import com.cksiow.ai.advisor.user.model.UserCreate;
import com.cksiow.ai.advisor.user.service.UserService;
import com.cksiow.ai.advisor.utils.ErrorUtils;
import com.universal.core.library.exception.BadRequestException;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OpenAIService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AssistantService assistantService;

    @Autowired
    OpenAIThreadService openAIThreadService;

    @Autowired
    UserService userService;

    @Autowired
    ThreadService threadService;

    @Value("${ai-advisor.system-key}")
    private String serverSystemKey;


    // Create a Pattern object
    Pattern patternPredictQuestion = Pattern.compile("@(.*?)(?=@|$|\n)");

    public OpenAIService() {
    }


    @Transactional(rollbackFor = Throwable.class)
    public List<OpenAIAssistantResponse> getAssistants(String uniqueId) {
        //pass unique id to get user's personal assistant id
        //get the last inserting assistant id (regardless is system or personal) to filter after
        var last = assistantService.getLastAssistant();
        //read 1st 20 assistants, and store the assistant id not in assistants table with createdBy = null (system)
        //convert to AssistantCreate
        var results = assistantService.convertCreate(assistantService.getOpenAISystemAssistants(100, last == null ? null : last.getId()));
        //save or update
        assistantService.saveAll(results);
        //directly read from database to get system assistant and person assistant
        //include personal assistants
        var user = uniqueId == null ? null : userService.findByUniqueId(uniqueId);
        return assistantService.getAvailableAssistants(user == null ? null : user.getId());
    }

    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public MessageResponse sendMessage(MessageRequest message) {
        var response = MessageResponse.builder()
                .assistantId(message.getAssistantId())
                .uniqueId(message.getUniqueId())
                .build();
        //first thing first, check is the uniqueId exists or not, if exists, is the credit > 0 or not
        if (message.getUniqueId() == null) {
            throw new BadRequestException("Unable to detect your device information. You may try resolving the issue by restarting the application.");
        }
        var user = userService.findByUniqueId(message.getUniqueId());
        if (user == null) {
            throw new BadRequestException("Your device is not registered in our system. Restarting the application may help resolve the issue.");
        } else if (user.getCredit() <= 0) {
            throw new BadRequestException("You don't have sufficient credits to continue using the service. To resume, you'll need to top up your credit.");
        }
        if (message.getAssistantId() == null) {
            throw new BadRequestException("Please choose the appropriate assistant from the menu at the top as we are unable to allocate the assistants you have selected.");

        }
        //if the thread ID is null then create thread 1st
        if (message.getThreadId() == null) {
            message.setThreadId(openAIThreadService.createNewThread());
            if (message.getThreadId() == null) {
                throw new BadRequestException("Thread creation failed, please retry.");
            }
        }
        response.setThreadId(message.getThreadId());
        //check is the thread exists, if not exists throw bad request
        if (!openAIThreadService.threadExists(message.getThreadId())) {
            throw new BadRequestException("The message thread does not exist. Please create a new message and retry.");
        }
        //if exists then prepare question
        var httpResult = openAIThreadService.prepareMessage(message);
        throwIfMessageFailSubmit(httpResult, "sendMessage prepareMessage: {}");
        //after that apply assistant id
        httpResult = openAIThreadService.assignAssistant(message);
        throwIfMessageFailSubmit(httpResult, "sendMessage assignAssistant: {}");
        var jsonObject = new JSONObject(httpResult.getContent());
        response.setRunId(jsonObject.getString("id"));
        if (response.getRunId() == null) {
            logger.error("sendMessage getRunId: {}", response);
            throw new BadRequestException("The message cannot be submitted. Please try again.");
        }
        //start check the run id status
        jsonObject = threadService.getThreadRunStatus(response);

        //if completed then read the last message
        response.setMessageId(jsonObject.getJSONArray("data").getJSONObject(0)
                .getJSONObject("step_details")
                .getJSONObject("message_creation")
                .getString("message_id"));
        //others info
        response.setCreateTimeStamp(jsonObject.getJSONArray("data").getJSONObject(0)
                .getInt("created_at"));
        // using RE to extract out the prediction question if exists
        httpResult = openAIThreadService.getMessage(response);
        throwIfMessageFailSubmit(httpResult, "sendMessage getMessage: {}");
        jsonObject = new JSONObject(httpResult.getContent());
        var messageContext = jsonObject.getJSONArray("content").getJSONObject(0)
                .getJSONObject("text").getString("value");
        Matcher m = patternPredictQuestion.matcher(messageContext);

        for (int i = 0; m.find(); i++) {
            response.getPredictQuestions().add(getTrimQuestion(m.group(0)));
        }
        response.setReplyContext(m.replaceAll("").trim());

        //store the thread id, unique id, assistant id and the message first 50 characters in the thread list table if thread id is not found
        threadService.save(ThreadCreate.builder()
                .threadId(response.getThreadId())
                .subject(message.getMessageContext().substring(0, Math.min(message.getMessageContext().length(), 50)))
                .createdBy(user.getId())
                .modifyBy(user.getId())
                .build());
        //deduct credit
        userService.deductCredit(user.getId());
        //set credit
        response.setCredit(user.getCredit() - 1);
        logger.info("finished run: {}:{}", response.getThreadId(), response.getRunId());
        return response;
    }

    private static String getTrimQuestion(String input) {
        return input.replaceAll("\n", "").replaceAll("@\\d+\\.", "").replaceAll("@", "").trim();
    }


    private void throwIfMessageFailSubmit(HttpResult resp, String error) {
        ErrorUtils.throwIfMessageFailSubmit(resp, error, "The message cannot be submitted. Please try again.");
    }

    public List<MessageResponse> getThreadMessage(String threadId, String uniqueId) {
        List<MessageResponse> results = new ArrayList<>();
        var thread = threadService.findByThreadId(threadId);
        var user = userService.findByUniqueId(uniqueId);
        //validate thread with user
        validateThread(thread, user);
        //get the thread data from openAPI
        var httpResult = openAIThreadService.getThread(threadId);
        //verify is existing or not
        ErrorUtils.throwIfMessageFailSubmit(httpResult, "getThreadMessage: {}", "Thread is not exists");
        var jsonObject = new JSONObject(httpResult.getContent());

        for (Object content : jsonObject.getJSONArray("data")) {
            JSONObject json = ((JSONObject) content).getJSONArray("content").getJSONObject(0);
            var messageContext = json.getJSONObject("text").getString("value");
            if (!messageContext.isEmpty()) {
                Matcher m = patternPredictQuestion.matcher(messageContext);
                var data = MessageResponse.builder().build();
                for (int i = 0; m.find(); i++) {
                    data.getPredictQuestions().add(getTrimQuestion(m.group(0)));
                }
                data.setReplyContext(m.replaceAll("").trim());
                //others info
                data.setThreadId(threadId);
                data.setRole(((JSONObject) content).getString("role"));
                data.setCreateTimeStamp(((JSONObject) content).getInt("created_at"));
                //fake alarm warning
                data.setAssistantId(((JSONObject) content).get("assistant_id").equals(null) ? null : ((JSONObject) content).getString("assistant_id"));
                results.add(data);
            }

        }
        return results;

    }

    private void validateThread(ThreadCreate thread, UserCreate user) {
        if (thread == null) {
            throw new BadRequestException("Thread is not exists");
        }
        if (user == null) {
            throw new BadRequestException("Your device is not registered in our system.");
        }
        if (!thread.getCreatedBy().equals(user.getId())) {
            throw new BadRequestException("This thread is not belong to your device.");
        }
    }

    @SneakyThrows
    public void updateAssistantsInstructions(String key) {
        if (!key.equals(serverSystemKey)) {
            throw new BadRequestException("Invalid key.");
        }


        //retrieve all assistants
        //retrieve from assistants tables
        var systemAssistants = assistantService.getSystemAssistants();
        for (AssistantResponse systemAssistant : systemAssistants) {
            if (!systemAssistant.getInstruction().contains(Constant.DEFAULT_APPEND_INSTRUCTIONS)) {
                //update open ai assistant data
                var oriInstructions = systemAssistant.getInstruction().split("\n");
                var id = systemAssistant.getAssistantId();
                var updateInstructions = (oriInstructions[0].startsWith("1. ") ? "" : "1. ") + oriInstructions[0] + "\n" + Constant.DEFAULT_APPEND_INSTRUCTIONS;
                systemAssistant.setInstruction(updateInstructions);
                assistantService.updateAssistant(id, systemAssistant.getInstruction());
                //update database data
                this.assistantService.saveAll(assistantService.convertCreate(List.of(systemAssistant)));
            }
        }

    }
}