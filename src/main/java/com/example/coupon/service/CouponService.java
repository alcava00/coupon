package com.example.coupon.service;

import com.example.coupon.Constants;
import com.example.coupon.api.CouponResource;
import com.example.coupon.exception.CouponException;
import com.example.coupon.model.Coupon;
import com.example.coupon.repository.CouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.DefaultSortParameters;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by alcava00 on 2018. 3. 29..
 */
@Service
public class CouponService {
    private final Logger logger = LoggerFactory.getLogger(CouponResource.class);

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UniqueIdGenerator uniqueIdGenerator;

    public Page<Coupon> getAllCouponOrderByIdDesc(Pageable pageable) {
        logger.debug("start sort by : {}", pageable);

        //sortedSet 과 hash 와 조인하여 order by 수행
        Page<Coupon> coupons = redisTemplate.execute(new RedisCallback<Page<Coupon>>() {
            @Override
            public Page<Coupon> doInRedis(RedisConnection connection) throws DataAccessException {

                long totalCount = connection.zCard(Constants.REDIS_KEY_COUPON_SSET.getBytes());
                int rowPerpage = pageable.getPageSize();
                int totalPage = (int) Math.floor(totalCount / rowPerpage) + 1;
                int page = pageable.getPageNumber() > totalPage ? totalPage : pageable.getPageNumber();
                int start = page * rowPerpage;

                // https://redis.io/commands/sort
                //sort parameter 세팅  , sort coupons by coupon:*  get coupon:*->id  get coupon:*->email .. .. DESC
                DefaultSortParameters defaultSortParameters = new DefaultSortParameters();
                defaultSortParameters.by(makeByPattern("id"));
                defaultSortParameters.addGetPattern(makeGetPattern("id"));
                defaultSortParameters.addGetPattern(makeGetPattern("email"));
                defaultSortParameters.addGetPattern(makeGetPattern("couponId"));
                defaultSortParameters.addGetPattern(makeGetPattern("createdDate"));
                defaultSortParameters.limit(start, rowPerpage);
                defaultSortParameters.order(SortParameters.Order.DESC);
                defaultSortParameters.numeric();

                if (logger.isDebugEnabled()) {
                    logger.debug(" Sorted SortParameters :{} ", defaultSortParameters.toString());
                }

                Iterator<byte[]> result = connection.sort(Constants.REDIS_KEY_COUPON_SSET.getBytes(), defaultSortParameters).iterator();
                if (result == null) {
                    return new PageImpl<Coupon>(null, pageable, totalCount);
                }

                //수행 결과 binding
                List<Coupon> coupons = new ArrayList<Coupon>();
                while (result.hasNext()) {
                    Coupon coupon = new Coupon();
                    Optional.ofNullable(result.next()).ifPresent(t -> coupon.setId(Integer.parseInt(new String(t))));
                    Optional.ofNullable(result.next()).ifPresent(t -> coupon.setEmail(new String(t)));
                    Optional.ofNullable(result.next()).ifPresent(t -> coupon.setCouponId(new String(t)));
                    Optional.ofNullable(result.next()).ifPresent(t -> coupon.setCreatedDate(new Date(Long.parseLong(new String(t)))));
                    coupons.add(coupon);
                }
                return new PageImpl<Coupon>(coupons, pageable, totalCount);
            }
        });

        logger.debug("total count : {}", coupons.getTotalElements());
        return coupons;
    }

    public Coupon getCouponById(@PathVariable String couponId) {
        return couponRepository.findOne(couponId);
    }

    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody Coupon coupon) throws URISyntaxException {
        //이메일 사용 여부 확인
        Optional.ofNullable(couponRepository.findByEmail(coupon.getEmail()))
                .ifPresent(issuedCoupon -> {
                    logger.debug(">>>>>>>>>>>>>> email ({}) is ", issuedCoupon.getEmail());
                    //E0001 쿠폰이 발급된 email 입니다.(coupon id: {0},email;{1})
                    throw new CouponException("E0001", issuedCoupon.getCouponId(), issuedCoupon.getEmail());
                });

        // id  sequence 생성
        long id = redisTemplate.boundValueOps(Constants.REDIS_KEY_COUPON_ID).increment(1);

        //save
        coupon.setId(id);
        coupon.setCouponId(uniqueIdGenerator.generateUniqueId());
        coupon.setCreatedDate(new Date());
        Coupon result = couponRepository.save(coupon);

        //for sorting
        redisTemplate.boundZSetOps(Constants.REDIS_KEY_COUPON_SSET).add(id, id);
        return ResponseEntity.created(new URI("/api/coupons/" + result.getCouponId())).body(result);
    }

    private byte[] makeGetPattern(String fieldName) {
        return String.format("%s:*->%s", Constants.REDIS_KEY_COUPON, fieldName).getBytes();
    }

    private byte[] makeByPattern(String fieldName) {
        return makeGetPattern(fieldName);
    }
}
