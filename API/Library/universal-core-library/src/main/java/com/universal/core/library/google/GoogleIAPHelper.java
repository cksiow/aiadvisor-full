package com.universal.core.library.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universal.core.library.google.iap.GooglePurchaseData;
import com.universal.core.library.utils.HttpClientHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;


@Component
public class GoogleIAPHelper {
    @Value("${google.api.url:https://www.googleapis.com}")
    String googleAPIUrl;

    @Autowired
    GoogleOAuth2Helper googleOAuth2Helper;

    public GooglePurchaseData validateIAP(String iapToken, String subcribeName, String refreshToken, String clientId, String clientSecret,
                                          String packageName,
                                          ObjectMapper objectMapper) {
        GooglePurchaseData purchase = null;
        var token = googleOAuth2Helper.getRefreshToken(refreshToken, clientId, clientSecret, objectMapper);
        if (token != null) {
            var purchaseData = HttpClientHelper.get(
                    googleAPIUrl + "/androidpublisher/v3/applications/" + packageName + "/purchases/subscriptions/" + subcribeName + "/tokens/"
                            + iapToken + "?access_token=" + token.getAccess_token());

            try {
                purchase = objectMapper.readValue(purchaseData.body(), GooglePurchaseData.class);
            } catch (Exception e) {
            }
            if (purchase != null && purchase.getExpiryTimeMillis() != null) {
                Timestamp ts = new Timestamp(Long.parseLong(purchase.getExpiryTimeMillis()));
                Date exp = new Date(ts.getTime());
                Calendar c = Calendar.getInstance();
                c.setTime(exp);
                c.add(Calendar.HOUR, 8);
                purchase.setExpiryTime(c.getTime().toInstant());
            }

        }
        return purchase;
    }
}
