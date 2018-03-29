package com.example.coupon.exception;

/**
 * Created by alcava00 on 2018. 3. 27..
 */
public class ExceptionResponse {
    private String errorCode;
    private String message;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
