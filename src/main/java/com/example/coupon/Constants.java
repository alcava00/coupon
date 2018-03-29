package com.example.coupon;

/**
 * Created by alcava00 on 2018. 3. 29..
 */
public  interface  Constants {
    /** spring-data-redis-keyvalue에서 coupon 정보 저장을 위해 사용  type : Hashes */
    String REDIS_KEY_COUPON = "coupon";
    /** for coupon list sorting,  type : Sorted sets */
    String REDIS_KEY_COUPON_SSET = "coupons";
    /** for coupon id , type:String */
    String REDIS_KEY_COUPON_ID = "sequence";

    String PROPERTIES_PREFIX="coupon";
}
