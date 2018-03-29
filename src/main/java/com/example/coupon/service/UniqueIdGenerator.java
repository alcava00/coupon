package com.example.coupon.service;

/**
 * IdGenerator
 * @see RandomUniqueIdGenerator
 *
 * Created by alcava00 on 2018. 3. 30..
 */
public interface UniqueIdGenerator {
      String generateUniqueId() ;
      default String getCheckDigit(String uniqueId) {
          return null;
      }
}
