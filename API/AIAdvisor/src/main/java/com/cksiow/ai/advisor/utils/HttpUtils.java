package com.cksiow.ai.advisor.utils;

import com.cksiow.ai.advisor.dto.HttpResult;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.io.CloseMode;

public class HttpUtils {

    @SneakyThrows
    public static HttpResult deleteHttpResult(String url, String openAIKey) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);

        // Adding headers to the request
        httpDelete.setHeader(new BasicHeader("Openai-Beta", "assistants=v1"));
        httpDelete.setHeader(new BasicHeader("Authorization", "Bearer " + openAIKey));

        HttpResult result = httpClient.execute(httpDelete, response -> {
            // Process response message and convert it into a value object
            return new HttpResult(response.getCode(), EntityUtils.toString(response.getEntity()));
        });
        httpClient.close(CloseMode.IMMEDIATE);
        return result;
    }

    @SneakyThrows
    public static HttpResult postHttpResult(String url, String openAIKey, String jsonBody) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // Adding headers to the request
        httpPost.setHeader(new BasicHeader("Openai-Beta", "assistants=v1"));
        httpPost.setHeader(new BasicHeader("Authorization", "Bearer " + openAIKey));

        if (jsonBody != null) {
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        }
        HttpResult result = httpClient.execute(httpPost, response -> {
            // Process response message and convert it into a value object
            return new HttpResult(response.getCode(), EntityUtils.toString(response.getEntity()));
        });
        httpClient.close(CloseMode.IMMEDIATE);
        return result;
    }

    @SneakyThrows
    public static HttpResult getHttpResult(String url, String openAIKey) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        // Adding headers to the request
        httpGet.setHeader(new BasicHeader("Openai-Beta", "assistants=v1"));
        httpGet.setHeader(new BasicHeader("Authorization", "Bearer " + openAIKey));
        final HttpResult result = httpClient.execute(httpGet, response -> {
            // Process response message and convert it into a value object
            return new HttpResult(response.getCode(), EntityUtils.toString(response.getEntity()));
        });
        httpClient.close(CloseMode.IMMEDIATE);
        return result;
    }
}
