package com.universal.core.library.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universal.core.library.google.token.GoogleAccessToken;
import com.universal.core.library.google.user.GoogleUserInfo;
import com.universal.core.library.utils.HttpClientHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class GoogleOAuth2Helper {

    @Value("${google.account.url:https://accounts.google.com}")
    String googleAccountUrl;

    @Value("${google.api.url:https://www.googleapis.com}")
    String googleAPIUrl;


    public GoogleAccessToken getRefreshToken(
            String refreshToken, String clientId, String clientSecret,
            ObjectMapper objectMapper) {
        Map<Object, Object> data = new HashMap<>();
        data.put("grant_type", "refresh_token");
        data.put("refresh_token", refreshToken);
        data.put("client_id", clientId);
        data.put("client_secret", clientSecret);
        Map<Object, Object> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        var accessToken = HttpClientHelper.post(googleAccountUrl + "/o/oauth2/token", data, headers);
        try {
            return objectMapper.readValue(accessToken.body(), GoogleAccessToken.class);
        } catch (Exception e) {
            return null;
        }
    }

    public GoogleUserInfo getUserInfo(String accessToken,
                                      ObjectMapper objectMapper) {
        Map<Object, Object> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla");
        var userInfo = HttpClientHelper.get(googleAPIUrl + "/oauth2/v1/userinfo?access_token=" + accessToken, headers);
        try {
            return objectMapper.readValue(userInfo.body(), GoogleUserInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

}
