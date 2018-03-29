package com.example.coupon.config;

import com.example.coupon.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;

/**
 * Created by alcava00 on 2018. 3. 30..
 */
@ConfigurationProperties(prefix = Constants.PROPERTIES_PREFIX)
public class CouponProperties {
    /**
     * defaultErrorCd : default: E0000
     */
    private String defaultErrorCode = "E0000";

    /**
     * default  HttpStatus  : default: HttpStatus.INTERNAL_SERVER_ERROR
     */
    private HttpStatus defaultExceptionHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    /** coupon id 생성시 사용할 문자셋 default : 0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ*/
    private String uniqueIdcharSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /** 구분자 제외를 제외한 coupon id 사이즈  */
    private int uniqueIdsize = 16;
    private int uniqueIdBlockSize = 4;
    /** coupon id 생성시 '-' 구분자 추가 여부 default:4 */
    private boolean useIdBlockSeparator = true;

    public boolean isUseIdBlockSeparator() {
        return useIdBlockSeparator;
    }

    public void setUseIdBlockSeparator(boolean useIdBlockSeparator) {
        this.useIdBlockSeparator = useIdBlockSeparator;
    }

    public String getUniqueIdcharSet() {
        return uniqueIdcharSet;
    }

    public void setUniqueIdcharSet(String uniqueIdcharSet) {
        this.uniqueIdcharSet = uniqueIdcharSet;
    }

    public int getUniqueIdsize() {
        return uniqueIdsize;
    }

    public void setUniqueIdsize(int uniqueIdsize) {
        this.uniqueIdsize = uniqueIdsize;
    }

    public int getUniqueIdBlockSize() {
        return uniqueIdBlockSize;
    }

    public void setUniqueIdBlockSize(int uniqueIdBlockSize) {
        this.uniqueIdBlockSize = uniqueIdBlockSize;
    }

    public String getDefaultErrorCode() {
        return defaultErrorCode;
    }

    public void setDefaultErrorCode(String defaultErrorCode) {
        this.defaultErrorCode = defaultErrorCode;
    }

    public HttpStatus getDefaultExceptionHttpStatus() {
        return defaultExceptionHttpStatus;
    }

    public void setDefaultExceptionHttpStatus(HttpStatus defaultExceptionHttpStatus) {
        this.defaultExceptionHttpStatus = defaultExceptionHttpStatus;
    }
}
