package com.example.coupon.api;

import com.example.coupon.model.Coupon;
import com.example.coupon.repository.CouponRepository;
import com.example.coupon.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by alcava00 on 2018. 3. 26..
 */
@RestController
@RequestMapping("/api/coupons")
public class CouponResource {
    private final Logger logger = LoggerFactory.getLogger(CouponResource.class);

    @Autowired
    private CouponService couponService;

    @GetMapping
    public DeferredResult<Page<Coupon>> getAllCoupons(Pageable pageable) {
        logger.debug(" call getAllCoupons >>>>> ");
        DeferredResult<Page<Coupon>> deferredResult = new DeferredResult<Page<Coupon>>();
        CompletableFuture.supplyAsync(() -> couponService.getAllCouponOrderByIdDesc(pageable))
                .whenCompleteAsync((result, throwable) -> deferredResult.setResult(result));
        return deferredResult;
    }

    @GetMapping("/{couponId}")
    public Coupon getCoupon(@PathVariable String couponId) {
        logger.debug("GetCoupon : {} ",couponId);
        return couponService.getCouponById(couponId);
    }

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody Coupon coupon) throws URISyntaxException {
        return couponService.createCoupon(coupon);
    }

}
