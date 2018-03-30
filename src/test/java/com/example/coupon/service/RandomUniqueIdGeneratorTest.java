package com.example.coupon.service;

import com.example.coupon.config.CouponProperties;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by alcava00 on 2018. 3. 26..
 */
public class RandomUniqueIdGeneratorTest extends Assert {
    private final String regExp= "^[\\w]{4}-[\\w]{4}-[\\w]{4}-[\\w]{4}$";

    private RandomUniqueIdGenerator randomUniqueIdGenerator=new RandomUniqueIdGenerator(new CouponProperties());
    /**
     * 결과값 포맷 체크
     * */
    @Test
    public void isValidFormat() throws Exception {
        long time=System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String id = randomUniqueIdGenerator.generateUniqueId();
            System.out.println(id);
            if (!id.matches(regExp)) {
                assertFalse(id, true);
            }
        }
    }


    /**
     * 중복값 존재 여부 확인
     * */
    @Test
    public void  checkDup() throws Exception {
         Set<String> set=new HashSet<>();
        for (int i = 0; i < 1000000; i++) {
            String id = randomUniqueIdGenerator.generateUniqueId();
            if(set.contains(id)){
                assertFalse(id, true);
            }else{
                set.add(id);
            }

        }
    }

}