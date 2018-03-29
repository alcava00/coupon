package com.example.coupon.repository;

import com.example.coupon.model.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by alcava00 on 2018. 3. 26..
 */
public interface CouponRepository extends PagingAndSortingRepository<Coupon, String> {
      Coupon findByEmail(String email);
}
