package com.example.coupon.service;

import com.example.coupon.config.CouponProperties;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by alcava00 on 2018. 3. 30..
 */
public class CustomRamdom {
    private static AtomicLong atomicLong = new AtomicLong();

    static {
        Executors.newFixedThreadPool(1).execute(() -> {
            while (true) {  atomicLong.incrementAndGet(); }
        });
    }

    public void nextBytes(byte[] bytes) {
        for (int i = 0, len = bytes.length; i < len; ) {
            for (long rnd = getLong();
                 rnd > 0 & i < len; rnd >>= Byte.SIZE) {
                bytes[i++] = (byte) rnd;
            }
        }
    }

    private long getLong() {
        long oldseed, nextseed;
        do {
            oldseed = atomicLong.get();
            nextseed = (722313 * oldseed + 2132131233);
        } while (!atomicLong.compareAndSet(oldseed, nextseed));
        return atomicLong.incrementAndGet();
    }

}
