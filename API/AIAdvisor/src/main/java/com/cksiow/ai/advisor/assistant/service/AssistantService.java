package com.cksiow.ai.advisor.assistant.service;


import com.cksiow.ai.advisor.assistant.dto.OpenAIAssistantResponse;
import com.cksiow.ai.advisor.assistant.dto.PersonalAssistantDeleteRequest;
import com.cksiow.ai.advisor.assistant.dto.PersonalAssistantRequest;
import com.cksiow.ai.advisor.assistant.model.AssistantCreate;
import com.cksiow.ai.advisor.assistant.model.AssistantResponse;
import com.cksiow.ai.advisor.assistant.repository.AssistantNativeRepository;
import com.cksiow.ai.advisor.assistant.repository.AssistantRepository;
import com.cksiow.ai.advisor.base.BaseAIAdvisorCRUDService;
import com.cksiow.ai.advisor.constant.Constant;
import com.cksiow.ai.advisor.dto.HttpResult;
import com.cksiow.ai.advisor.dto.MessageRequest;
import com.cksiow.ai.advisor.dto.MessageResponse;
import com.cksiow.ai.advisor.service.OpenAIThreadService;
import com.cksiow.ai.advisor.thread.service.ThreadService;
import com.cksiow.ai.advisor.user.service.UserService;
import com.cksiow.ai.advisor.utils.ErrorUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.universal.core.library.base.model.BaseModel;
import com.universal.core.library.exception.BadRequestException;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cksiow.ai.advisor.utils.HttpUtils.getHttpResult;
import static com.cksiow.ai.advisor.utils.HttpUtils.postHttpResult;

@Service
public class AssistantService extends BaseAIAdvisorCRUDService<AssistantRepository, AssistantCreate, AssistantResponse> {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${openai.key}")
    private String openAIKey;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AssistantNativeRepository assistantNativeRepository;

    @Autowired
    UserService userService;

    @Value("${openai.keyword-description-assistant-id}")
    private String keywordDescriptionAssistantId;

    @Autowired
    OpenAIThreadService openAIThreadService;

    @Autowired
    ThreadService threadService;


    @SneakyThrows
    public HttpResult getAssistants(Integer limit, String afterAssistantId) {
        //read all assistants
        return getHttpResult(String.format("https://api.openai.com/v1/assistants?limit=%s&order=asc&after=%s", limit, afterAssistantId == null ? "" : afterAssistantId), openAIKey);

    }

    public ArrayList<AssistantResponse> getOpenAISystemAssistants(Integer limit, String afterAssistantId) {
        var results = new ArrayList<AssistantResponse>();
        var allAssistants = this.getAssistants(limit, afterAssistantId);
        ErrorUtils.throwIfMessageFailSubmit(allAssistants, "getSystemAssistants: {}", "Assistants retrieve error");
        var response = new JSONObject(allAssistants.getContent());
        //convert the results to response and return
        JSONArray dataArray = response.getJSONArray("data");

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataObject = dataArray.getJSONObject(i);
            if (!dataObject.getString("name").startsWith("_")) {
                results.add(AssistantResponse.builder()
                        .assistantId(dataObject.getString("id"))
                        .name(dataObject.getString("name"))
                        .instruction(dataObject.getString("instructions"))
                        .build());
            }

        }
        return results;
    }

    public List<AssistantResponse> getSystemAssistants() {
        return this.getRepository().findSystemAssistants();
    }

    //update assistants appending our preset message
    @SneakyThrows
    public HttpResult updateAssistant(String id, String instructions) {
        return postHttpResult(String.format("https://api.openai.com/v1/assistants/%s", id)
                , openAIKey, objectMapper.writeValueAsString(getPrepareMessageJson(instructions))
        );
    }

    private static Map<String, Object> getPrepareMessageJson(String instructions) {
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("instructions", instructions);
        return jsonObject;
    }


    public AssistantService(AssistantRepository repo) {
        super(repo);
    }

    @Override
    protected void assignCreateInfo(BaseModel data) {

    }

    @Override
    protected void validateDelete(String id) {

    }

    @Override
    protected void validateBatch(List<AssistantCreate> list) {

    }

    @Override
    protected void validate(AssistantCreate data) {

        if (data.getAssistantId() == null) {
            throw new BadRequestException("assistant id not allow null");
        }
        if (data.getName() == null) {
            throw new BadRequestException("name not allow null");
        }
        if (data.getFirstInstruction() == null) {
            throw new BadRequestException("first instruction not allow null");
        }
    }

    @Override
    protected Object validateBefore(AssistantCreate data) {
        //check if exists then set id to make it update
        if (data.getAssistantId() != null) {
            var exists = this.getRepository().findByAssistantId(data.getAssistantId());
            if (exists != null) {
                data.setId(exists.getId());
            }

        }
        return data;
    }

    @Override
    protected AssistantCreate afterSave(AssistantCreate data, Object beforeSave) {
        return data;
    }

    @Override
    public AssistantCreate save(AssistantCreate data) {
        return super.save(data);
    }


    public AssistantCreate findByAssistantId(String assistantId) {
        return this.getRepository().findByAssistantId(assistantId);
    }

    public List<AssistantCreate> convertCreate(List<AssistantResponse> responses) {
        List<AssistantCreate> results = new ArrayList<>();
        for (AssistantResponse res : responses) {
            results.add(AssistantCreate.builder()
                    .assistantId(res.getAssistantId())
                    .name(res.getName())
                    .firstInstruction(res.getInstruction().split("\n")[0])
                    .instruction(res.getInstruction())
                    .build());
        }
        return results;
    }

    public List<OpenAIAssistantResponse> getAvailableAssistants(String userId) {
        return this.assistantNativeRepository.findAvailableAssistants(userId);

    }

    public OpenAIAssistantResponse getLastAssistant() {
        return this.assistantNativeRepository.findLastAssistant();
    }

    @Transactional(rollbackFor = Throwable.class)
    public OpenAIAssistantResponse createPersonalAssistant(PersonalAssistantRequest request) {
        if (request.getKeywords() == null) {
            throw new BadRequestException("Keyword cannot be blank");
        }
        if (request.getUniqueId() == null) {
            throw new BadRequestException("Unable to detect your device information. You may try resolving the issue by restarting the application.");
        }
        //verify uniqueId and name, credit
        var user = userService.findByUniqueId(request.getUniqueId());
        if (user == null) {
            throw new BadRequestException("Unable to detect your device information. You may try resolving the issue by restarting the application.");
        } else if (user.getCredit() <= 0) {
            throw new BadRequestException("You don't have sufficient credits to continue using the service. To resume, you'll need to top up your credit.");
        }
        //get keyword master generated firstInstructions
        var firstInstructions = this.getKeywordInstructions(request);
        //if it is invalid firstInstructions
        if (!firstInstructions.startsWith("1. ")) {
            throw new BadRequestException("Keyword is unclear, provide more information to continue");
        }
        //crate in openAI
        var instructions = firstInstructions + "\n" + Constant.DEFAULT_APPEND_INSTRUCTIONS;
        var httpResult = openAIThreadService.createAssistant(request.getKeywords(), instructions);
        ErrorUtils.throwIfMessageFailSubmit(httpResult, "createPersonalAssistant : {}", "The request cannot be process. Please try again.");

        var jsonObject = new JSONObject(httpResult.getContent());
        //save to database
        this.save(AssistantCreate.builder()
                .name(request.getKeywords())
                .assistantId(jsonObject.getString("id"))
                .firstInstruction(firstInstructions)
                .instruction(instructions)
                .createdBy(user.getId())
                .modifyBy(user.getId())
                .build());
        //deduct credit
        userService.deductCredit(user.getId());
        logger.info("finished run: {}:{}", request.getKeywords(), request.getUniqueId());
        return OpenAIAssistantResponse.builder()
                .name(request.getKeywords())
                .id(jsonObject.getString("id"))
                .build();
    }

    private String getKeywordInstructions(PersonalAssistantRequest request) {
        var message = MessageRequest.builder()
                .messageContext(request.getKeywords())
                .threadId(openAIThreadService.createNewThread())
                .assistantId(keywordDescriptionAssistantId)
                .build();
        if (message.getThreadId() == null) {
            throw new BadRequestException("Thread creation failed, please retry.");
        }
        message.setMessageContext(request.getKeywords());
        var httpResult = openAIThreadService.prepareMessage(message);
        ErrorUtils.throwIfMessageFailSubmit(httpResult, "getKeywordInstructions prepareMessage: {}", "The request cannot be process. Please try again.");
        //after that apply assistant id
        httpResult = openAIThreadService.assignAssistant(message);
        ErrorUtils.throwIfMessageFailSubmit(httpResult, "getKeywordInstructions assignAssistant: {}", "The request cannot be process. Please try again.");
        var jsonObject = new JSONObject(httpResult.getContent());
        var response = MessageResponse.builder()
                .threadId(message.getThreadId())
                .assistantId(keywordDescriptionAssistantId)
                .build();
        response.setRunId(jsonObject.getString("id"));
        if (response.getRunId() == null) {
            logger.error("updateAssistantsInstructions getRunId: {}", response);
            throw new BadRequestException("The message cannot be submitted. Please try again.");
        }


        var data = threadService.getThreadRunStatus(response);
        response.setMessageId(data.getJSONArray("data").getJSONObject(0)
                .getJSONObject("step_details")
                .getJSONObject("message_creation")
                .getString("message_id"));
        //others info
        response.setCreateTimeStamp(data.getJSONArray("data").getJSONObject(0)
                .getInt("created_at"));
        // using RE to extract out the prediction question if exists
        httpResult = openAIThreadService.getMessage(response);
        ErrorUtils.throwIfMessageFailSubmit(httpResult, "getKeywordInstructions getMessage: {}", "The request cannot be process. Please try again.");
        jsonObject = new JSONObject(httpResult.getContent());
        return jsonObject.getJSONArray("content").getJSONObject(0)
                .getJSONObject("text").getString("value");
    }

    public List<OpenAIAssistantResponse> getPersonalAssistant(String uniqueId) {
        //get person assistant data
        return this.assistantNativeRepository.findByUniqueId(uniqueId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deletePersonalAssistant(PersonalAssistantDeleteRequest request) {
        //verify
        if (request.getAssistantId() == null) {
            throw new BadRequestException("Assistant id cannot be blank");
        }
        if (request.getUniqueId() == null) {
            throw new BadRequestException("Unable to detect your device information. You may try resolving the issue by restarting the application.");
        }
        //verify uniqueId and name, credit
        var user = userService.findByUniqueId(request.getUniqueId());
        if (user == null) {
            throw new BadRequestException("Unable to detect your device information. You may try resolving the issue by restarting the application.");
        }
        var assistant = this.findByAssistantId(request.getAssistantId());
        if (assistant == null) {
            throw new BadRequestException("Assistant cannot be found, probably is removed");
        }
        if (assistant.getCreatedBy() == null || !assistant.getCreatedBy().equals(user.getId())) {
            throw new BadRequestException("Access denied");
        }
        //remove from database 1st
        this.getRepository().deleteById(assistant.getId());
        //remove from openAI, regardless fail or success also never-mind
        openAIThreadService.deleteAssistant(assistant.getAssistantId());
    }
}