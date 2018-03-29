package com.dream.interceptor;

import com.dream.dto.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionResolver{

    private static final Logger logger = LoggerFactory.getLogger(ExceptionResolver.class);

    @ResponseBody
    @ExceptionHandler(value = CommonException.class)
    public RestResponse commonExceptionHandler(CommonException e) {
        logger.info(e.getMessage());
        return RestResponse.failure(e);
    }

}
