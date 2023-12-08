package com.universal.core.library.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public abstract class BaseHttpException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final int errorCode;

    public BaseHttpException(String exception, HttpStatus status) {
        super(exception);
        this.errorCode = status.value();
    }

}
