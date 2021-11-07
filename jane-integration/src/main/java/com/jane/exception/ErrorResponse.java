package com.jane.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

@Data
@EqualsAndHashCode(callSuper = false)
public class ErrorResponse extends RuntimeException {
    private String messageKey;
    private Object[] args;
    private int code;

    public ErrorResponse(int code, String messageKey, @Nullable Object[] args) {
        this.messageKey = messageKey;
        this.args = args;
        this.code = code;
    }

    public ErrorResponse(HttpStatus code, String messageKey) {
        this.code = code.value();
        this.messageKey = messageKey;
    }

    public ErrorResponse(HttpStatus code) {
        this.code = code.value();
    }

    public ErrorResponse(int code, String messageKey) {
       this.code=code;
       this.messageKey=messageKey;
    }
}
