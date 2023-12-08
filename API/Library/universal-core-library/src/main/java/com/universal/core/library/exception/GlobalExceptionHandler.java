package com.universal.core.library.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public ExceptionInfo handleIllegalArgumentException(IllegalArgumentException ex) {
        return handleExceptionInternal(ex);
    }

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<ExceptionInfo> handleResponseStatusException(ResponseStatusException ex) {
        ExceptionInfo errorInfo = handleExceptionInternal(ex);
        return new ResponseEntity<>(errorInfo, ex.getStatus());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = JdbcUpdateAffectedIncorrectNumberOfRowsException.class)
    @ResponseBody
    public ExceptionInfo handleJdbcUpdateAffectedIncorrectNumberOfRowsException(Exception ex) {
        return handleExceptionInternal(ex);
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ExceptionInfo handleException(Exception ex) {
        logger.error("Internal system error", ex);
        return handleExceptionInternal(new Exception("System error"));
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BadRequestException.class)
    @ResponseBody
    public ExceptionInfo handleException(BadRequestException ex) {
        return handleExceptionInternal(ex, ex.getMessage(), ex.getData());
    }

    private ExceptionInfo handleExceptionInternal(Exception ex) {
        return handleExceptionInternal(ex, ex.getMessage(), null);

    }

    private ExceptionInfo handleExceptionInternal(Exception ex, String message, Object data) {
        logger.warn("Error happen: {} : {}", ex.getMessage(), message);
        return new ExceptionInfo("Error", message, data);

    }

}
