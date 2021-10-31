package com.jane.advice;

import com.jane.exception.ErrorResponse;
import com.jane.pojo.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ErrorControllerAdvice {

    @ExceptionHandler(ErrorResponse.class)
    public ResponseEntity<?> handle(ErrorResponse e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(e.getMessageKey());
        apiResponse.setCode(e.getCode());
        return ResponseEntity.status(e.getCode()).body(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handle(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);

        ApiResponse<String> apiResponse = new ApiResponse<>();
        if (e.getCause() != null) {
            apiResponse.setMessage(e.getCause().getMessage());
        } else {
            apiResponse.setMessage(e.getMessage());
        }
        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handle(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);

        ApiResponse<String> apiResponse = new ApiResponse<>();
        if (e.getCause() != null) {
            apiResponse.setMessage(e.getCause().getMessage());
        } else {
            apiResponse.setMessage(e.getMessage());
        }
        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

}
