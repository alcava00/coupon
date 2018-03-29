package com.example.coupon.exception;

import org.springframework.http.HttpStatus;

/**
 * 업무 코드에서 발생하는 논리적인 오류 처리
 * Created by alcava00 on 2018. 3. 27..
 */
public class CouponException extends RuntimeException {
    /** 오류 코드 */
    private String errorCd;
    /** 오류 메세지 arguments  */
    private String[] messageArg;
    /** httpStatus */
    private HttpStatus httpStatus;

    public CouponException(String errorCd, String... messageArg) {
        super(errorCd);
        this.errorCd = errorCd;
        this.messageArg = messageArg;
    }

    public CouponException(Throwable cause, String errorCd, String... messageArgs) {
        super(cause);
        this.errorCd = errorCd;
        this.messageArg = messageArgs;
    }

    public String getErrorCd() {
        return errorCd;
    }

    public CouponException setErrorCd(String errorCd) {
        this.errorCd = errorCd;
        return this;
    }

    public String[] getMessageArg() {
        return messageArg;
    }

    public CouponException setMessageArg(String[] messageArg) {
        this.messageArg = messageArg;
        return this;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public CouponException setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }
}
