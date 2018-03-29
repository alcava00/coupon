package com.example.coupon.service;

import com.example.coupon.config.CouponProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.SecureRandom;

/**
 * Created by alcava00 on 2018. 3. 26..
 */
public class RandomUniqueIdGenerator implements UniqueIdGenerator {

    private char[] charSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private int size = 16;
    private int idBlockSize = 4;
    private int charSetSize = charSet.length;
    private boolean useIdBlockSeparator=true;

    private static char div = '-';

    @Autowired
    private CouponProperties couponProperties;

    public RandomUniqueIdGenerator(CouponProperties couponProperties) {
        charSet = couponProperties.getUniqueIdcharSet().toCharArray();
        size = couponProperties.getUniqueIdsize();
        charSetSize = charSet.length;
        useIdBlockSeparator= couponProperties.isUseIdBlockSeparator();
    }

    public String generateUniqueId() {

        byte[] randomByte = new byte[size];  //16자리의 byte[]
        int resultSize =useIdBlockSeparator? size + (size / idBlockSize) - 1:size; //구분자를 포함한 길이
        char[] resultArray = new char[resultSize];  //결과값 저장

        SecureRandom randomno = new SecureRandom();
        randomno.nextBytes(randomByte);

        int resultArrayPosition = 0; // resultArray
        for (int i = 0; i < resultSize; i++) {
            //idBlockSize 마다 구분자 추가
            if (useIdBlockSeparator&& i % (idBlockSize + 1) == idBlockSize) {
                resultArray[i] = div;
            } else {
                resultArray[i] = charSet[(randomByte[resultArrayPosition] & 0xff) % charSetSize];
                resultArrayPosition++;
            }
        }

        return new String(resultArray);
    }

//    private static void nextBytes(byte[] bytes) {
//        int len = bytes.length;
//        for (int i = 0; i < len ;) {
//            for (int rnd =  (int)(System.nanoTime()+(i*123)); rnd > 0 && i < len; rnd >>= Byte.SIZE) {
//                bytes[i++] = (byte)(rnd);
//            }
//        }
//    }

}
