package com.universal.core.library.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universal.core.library.google.fcm.GoogleFirebaseMessageBody;
import com.universal.core.library.google.fcm.GoogleFirebaseMessageBodyNotification;
import com.universal.core.library.google.fcm.GoogleFirebaseResponseBody;
import com.universal.core.library.utils.HttpClientHelper;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {

    @SneakyThrows
    public static GoogleFirebaseResponseBody sendTopicNotification(String key, String to, String title
            , String body, Object data, ObjectMapper objectMapper) {
        // send fcm message
        var sendBody = new GoogleFirebaseMessageBody();
        sendBody.setTo(to);
        sendBody.setDelay_while_idle(false);
        sendBody.setPriority("high");
        if (title != null) {
            sendBody.setNotification(GoogleFirebaseMessageBodyNotification.builder()
                    .title(title)
                    .body(body)
                    .build());
        }
        sendBody.setData(data);

        var result = HttpClientHelper.post("https://fcm.googleapis.com/fcm/send", sendBody, getHeaders(key));
        return objectMapper.readValue(result.body(), GoogleFirebaseResponseBody.class);
    }

    private static Map<Object, Object> getHeaders(String key) {
        Map<Object, Object> data = new HashMap<>();
        data.put("Authorization",
                "key=" + key);
        data.put("Content-Type", "application/json");
        return data;
    }
}
