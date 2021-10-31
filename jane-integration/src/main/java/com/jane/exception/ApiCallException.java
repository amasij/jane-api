package com.jane.exception;

public class ApiCallException extends RuntimeException {

    protected final String url;

    protected final int code;

    protected final String message;

    protected final String contentType;

    protected ApiCallException(String url, int code, String message, String contentType) {
        super(String.format("Service call to %s failed with status code %d: %s", url, code, contentType));
        this.url = url;
        this.code = code;
        this.message = message;
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public int getCode() {
        return code;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return null;
    }
}
