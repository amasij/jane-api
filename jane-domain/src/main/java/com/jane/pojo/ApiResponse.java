package com.jane.pojo;

public class ApiResponse<T> {

    private String status;
    private String message;
    private Integer code;
    private T data;

    public ApiResponse(int code, String message, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public ApiResponse(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public ApiResponse() {
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
