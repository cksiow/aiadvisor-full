package com.universal.core.library.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExceptionInfo {
    Object errorCode;
    String errorMessage;
    Object data;

    public ExceptionInfo(Object errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

    }

    public ExceptionInfo(Object errorCode, String errorMessage, Object data) {
        this(errorCode, errorMessage);
        this.data = data;
    }
}
