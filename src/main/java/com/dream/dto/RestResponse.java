package com.dream.dto;


import com.dream.interceptor.CommonException;

public class RestResponse<T> {

    private String success;
    private String message;
    private T data;

    private RestResponse() {
    }

    private RestResponse(String success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> RestResponse success() {
        return new RestResponse<T>("1", "SUCCESS", null);
    }

    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>("0", "SUCCESS", data);
    }

    public static <T> RestResponse<T> failure(CommonException e) {
        return new RestResponse<>("0", e.getMessage(), null);
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
