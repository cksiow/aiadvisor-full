package com.universal.core.library.google.iap;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = false)
public class GooglePurchaseData {

    private static final long serialVersionUID = 1L;
    private String expiryTimeMillis;

    Instant expiryTime;

    String status;

}