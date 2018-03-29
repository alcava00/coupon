package com.example.coupon.service;

import com.example.coupon.config.CouponProperties;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by alcava00 on 2018. 3. 26..
 */
public class RandomUniqueIdGeneratorTest extends Assert {
//    private final String regExp = "[a-zA-Z0-9]{16}";
    private final String regExp= "^[\\w]{4}-[\\w]{4}-[\\w]{4}-[\\w]{4}$";

    private RandomUniqueIdGenerator randomUniqueIdGenerator=new RandomUniqueIdGenerator(new CouponProperties());

    @Test
    public void isValidFormat() throws Exception {
        for (int i = 0; i < 100000; i++) {
            String id = randomUniqueIdGenerator.generateUniqueId();
            if (!id.matches(regExp)) {
                assertFalse(id, true);
            }
        }
    }

}