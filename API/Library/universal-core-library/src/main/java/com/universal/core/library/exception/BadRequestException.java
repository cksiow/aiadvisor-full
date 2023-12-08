package com.universal.core.library.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@JsonIgnoreProperties(value = {"cause", "stackTrace", "suppressed", "localizedMessage"})
public class BadRequestException extends BaseHttpException {

    private static final long serialVersionUID = 1433367593443708095L;

    private final Object data;

    public BadRequestException(String exception) {
        super(exception, HttpStatus.BAD_REQUEST);
        this.data = null;
    }

    public BadRequestException(String exception, Object data) {
        super(exception, HttpStatus.BAD_REQUEST);
        this.data = data;
    }


    public BadRequestException(String exception, Object data, HttpStatus status) {
        super(exception, status);
        this.data = data;
    }

}
