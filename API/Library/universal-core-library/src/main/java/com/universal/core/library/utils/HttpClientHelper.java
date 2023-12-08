package com.universal.core.library.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClientHelper {

    private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static HttpResponse<String> get(String url) {
        return HttpClientHelper.get(url, new HashMap<>());
    }

    public static HttpResponse<String> get(String url, Map<Object, Object> headers) {

        var builder = HttpRequest.newBuilder().GET().uri(URI.create(url)).setHeader("User-Agent", "Mozilla");
        for (Map.Entry<Object, Object> entry : headers.entrySet()) {
            builder = builder.setHeader(entry.getKey().toString(), entry.getValue().toString());
        }
        HttpRequest request = builder.build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (IOException | InterruptedException e) {
            return null;
        }

    }

    public static HttpResponse<String> post(String url, Map<Object, Object> data, Map<Object, Object> headers) {

        return HttpClientHelper.post(url, buildFormDataFromMap(data), headers);

    }

    public static HttpResponse<String> post(String url, String body, Map<Object, Object> headers) {

        return HttpClientHelper.post(url, HttpRequest.BodyPublishers.ofString(body), headers);

    }

    public static HttpResponse<String> post(String url, Object data, Map<Object, Object> headers) {

        try {
            return HttpClientHelper.post(url, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(data)), headers);
        } catch (JsonProcessingException e) {
            return null;
        }

    }


    public static HttpResponse<String> post(String url, HttpRequest.BodyPublisher bodyPublisher, Map<Object, Object> headers) {

        HttpRequest.Builder builder = HttpRequest.newBuilder().POST(bodyPublisher).uri(URI.create(url));

        for (Map.Entry<Object, Object> entry : headers.entrySet()) {
            builder = builder.setHeader(entry.getKey().toString(), entry.getValue().toString());
        }

        HttpRequest request = builder.build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (IOException | InterruptedException e) {
            return null;
        }

    }

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        System.out.println(builder.toString());
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    public static HttpResponse<String> delete(String url, Map<Object, Object> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().DELETE().uri(URI.create(url));

        for (Map.Entry<Object, Object> entry : headers.entrySet()) {
            builder = builder.setHeader(entry.getKey().toString(), entry.getValue().toString());
        }

        HttpRequest request = builder.build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (IOException | InterruptedException e) {
            return null;
        }

    }

}
