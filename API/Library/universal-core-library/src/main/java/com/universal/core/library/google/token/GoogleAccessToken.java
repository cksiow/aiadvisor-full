package com.universal.core.library.google.token;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoogleAccessToken {
    private static final long serialVersionUID = 1L;

    private String access_token;

}