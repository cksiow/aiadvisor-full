package com.cksiow.ai.advisor.utils;

import com.cksiow.ai.advisor.dto.HttpResult;
import com.universal.core.library.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorUtils {
    static Logger logger = LoggerFactory.getLogger(ErrorUtils.class);

    public static void throwIfMessageFailSubmit(HttpResult resp, String error, String message) {
        if (!resp.getStatus().equals(200)) {
            logger.error(error, resp);
            throw new BadRequestException(message);
        }
    }

}
