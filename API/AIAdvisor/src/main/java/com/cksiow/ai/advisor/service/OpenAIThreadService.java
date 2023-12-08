package com.cksiow.ai.advisor.service;


import com.cksiow.ai.advisor.dto.HttpResult;
import com.cksiow.ai.advisor.dto.MessageRequest;
import com.cksiow.ai.advisor.dto.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.cksiow.ai.advisor.utils.HttpUtils.*;


@Service
public class OpenAIThreadService {
    @Value("${openai.key}")
    private String openAIKey;

    @Autowired
    ObjectMapper objectMapper;


    public String createNewThread() {
        //create new thread
        var result = postHttpResult("https://api.openai.com/v1/threads", openAIKey, null);
        var object = new JSONObject(result.getContent());
        return !object.isNull("id") ? object.getString("id") : null;
    }

    public boolean threadExists(String threadId) {
        //check is thread exists
        var result = getHttpResult(String.format("https://api.openai.com/v1/threads/%s", threadId), openAIKey);
        return result.getStatus().equals(200);
    }

    @SneakyThrows
    public HttpResult prepareMessage(MessageRequest message) {
        var jsonObject = getPrepareMessageJson(message);
        //prepare message
        return postHttpResult(String.format("https://api.openai.com/v1/threads/%s/messages", message.getThreadId())
                , openAIKey, objectMapper.writeValueAsString(jsonObject));
    }

    private static Map<String, Object> getPrepareMessageJson(MessageRequest message) {
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("role", "user");
        jsonObject.put("content", message.getMessageContext());
        return jsonObject;
    }

    @SneakyThrows
    public HttpResult assignAssistant(MessageRequest message) {
        var jsonObject = getAssignAssistantJson(message);
        //assign assistant
        return postHttpResult(String.format("https://api.openai.com/v1/threads/%s/runs", message.getThreadId())
                , openAIKey, objectMapper.writeValueAsString(jsonObject));
    }

    private static Map<String, Object> getAssignAssistantJson(MessageRequest message) {
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("assistant_id", message.getAssistantId());
        return jsonObject;
    }

    @SneakyThrows
    public HttpResult getRunResult(MessageResponse response) {
        return getHttpResult(
                String.format(
                        "https://api.openai.com/v1/threads/%s/runs/%s/steps"
                        , response.getThreadId(), response.getRunId()
                )
                , openAIKey);
    }

    public HttpResult getMessage(MessageResponse response) {
        return getHttpResult(
                String.format(
                        "https://api.openai.com/v1/threads/%s/messages/%s"
                        , response.getThreadId(), response.getMessageId()
                )
                , openAIKey);
    }

    public void cancelRun(MessageResponse response) {
        postHttpResult(
                String.format(
                        "https://api.openai.com/v1/threads/%s/runs/%s/cancel"
                        , response.getThreadId(), response.getRunId()
                )
                , openAIKey, null);
    }

    public HttpResult getThread(String threadId) {
        //
        return getHttpResult(
                String.format(
                        "https://api.openai.com/v1/threads/%s/messages?limit=100&order=asc"
                        , threadId
                )
                , openAIKey);
    }

    @SneakyThrows
    public HttpResult createAssistant(String keywords, String instructions) {
        return postHttpResult("https://api.openai.com/v1/assistants"
                , openAIKey, objectMapper.writeValueAsString(getAssistantRequest(keywords, instructions)));
    }

    private static Map<String, Object> getAssistantRequest(String name, String instructions) {
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("name", name);
        jsonObject.put("model", "gpt-3.5-turbo-1106");
        jsonObject.put("instructions", instructions);
        return jsonObject;
    }

    public HttpResult deleteAssistant(String assistantId) {
        return deleteHttpResult(String.format("https://api.openai.com/v1/assistants/%s", assistantId)
                , openAIKey);
    }
}
