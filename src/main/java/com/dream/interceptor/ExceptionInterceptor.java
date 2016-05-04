package com.dream.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by knight on 16/5/4.
 */
//@ControllerAdvice
public class ExceptionInterceptor {

    @ResponseStatus(value= HttpStatus.OK)
    public static class UserNotFoundException extends RuntimeException {}

    @ExceptionHandler(HttpSessionRequiredException.class)
    @ResponseBody
    public Object handleConflict(HttpSessionRequiredException exception) throws HttpSessionRequiredException {
        System.out.println(exception.getLocalizedMessage());
        if(exception.getMessage().contains("currentManager")){
            Map<String, Object> res = new HashMap<>();
            res.put("success",0);
            res.put("message","请先登录");
            res.put("error","000");
            return res;
        }else{

            throw exception;

        }

    }

}
