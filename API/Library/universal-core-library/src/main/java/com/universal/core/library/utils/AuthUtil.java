package com.universal.core.library.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universal.core.library.exception.ExceptionInfo;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


public class AuthUtil {

    private AuthUtil() {

    }

    @SneakyThrows
    public static void setAuthExceptionResponse(HttpServletResponse response, ObjectMapper mapper, String message, Object data) {
        response.setStatus(data == null ? UNAUTHORIZED.value() : BAD_REQUEST.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getOutputStream().write(
                mapper.writeValueAsBytes(new ExceptionInfo(data == null ? UNAUTHORIZED.value() : BAD_REQUEST.value(), message, data))
        )
        ;
    }
}
