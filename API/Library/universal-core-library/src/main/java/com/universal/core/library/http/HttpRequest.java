package com.universal.core.library.http;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpRequest {

    @SneakyThrows
    public static String get(String url) {
        // create a client
        var client = HttpClient.newHttpClient();
        // create a request
        var request = java.net.http.HttpRequest.newBuilder(
                URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .build();

        // use the client to send the request
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
