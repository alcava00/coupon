package com.example.coupon.api;

import com.example.coupon.Constants;
import com.example.coupon.model.Coupon;
import com.example.coupon.repository.CouponRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by alcava00 on 2018. 3. 26..
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CouponResourceTest extends Assert {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    private MockMvc restCouponMockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Coupon coupon = null;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.restCouponMockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @org.junit.After
    public void after() {
        Optional.ofNullable(coupon).ifPresent(coupon1 -> couponRepository.delete(coupon));
    }

    @Test
    public void testGetAllcouponse() {
        List<Coupon> createdCoupon = new ArrayList<Coupon>();
        //조회 대상이 없으면 ...
        if (redisTemplate.boundZSetOps(Constants.REDIS_KEY_COUPON_SSET).zCard() < 200) {
            for (int i = 0; i < 200; i++) {
                createdCoupon.add(sendCreateRequest());
            }
        }
        try {
            ResponseEntity<Map> body = this.restTemplate.getForEntity("/api/coupons", Map.class);
            assertEquals((String) body.getBody().toString(), HttpStatus.OK, body.getStatusCode());
            System.out.println("body.getBody()>>" + body.getBody());

            //paging check
            int rowPerPage = (int) body.getBody().get("size");
            int totalCount = (int) body.getBody().get("totalElements");
            int totalPages = (int) body.getBody().get("totalPages");
            int returnCount = ((List) body.getBody().get("content")).size();
            long totalCount2 = redisTemplate.boundZSetOps(Constants.REDIS_KEY_COUPON_SSET).zCard();

            // 총건수 확인
            assertEquals(totalCount, totalCount2);

            // return elements 건수 확인
            if (totalCount > rowPerPage) {
                assertEquals(rowPerPage, returnCount);
            } else {
                assertEquals(totalCount, returnCount);
            }

            //page count
            assertEquals(totalPages, (int) Math.floor(totalCount / rowPerPage) + 1);

            //sorting check
            List contents = (List) body.getBody().get("content");
            Object[] ids = (Object[]) contents.stream().map(couponMap -> objectMapper.convertValue(couponMap, Coupon.class))
                    .map(coupon -> ((Coupon) coupon).getId()).toArray();  //조회된 결과에서 id 를 추출
            Object[] tmp = ids.clone();
            Arrays.sort(tmp);
            ArrayUtils.reverse(tmp);
            assertArrayEquals("sorting error:  ", ids, tmp);//소팅된 값과 조회된 결과 비교
        } finally {
            createdCoupon.stream().forEach(coupon1 -> sendDeleteRequest(coupon1.getCouponId()));
        }
    }

    @Test
    public void createCoupon() throws Exception {
        Coupon _coupon = new Coupon();
        _coupon.setEmail(System.currentTimeMillis() + "a@empal.com");

        byte[] returnValue = restCouponMockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(_coupon))
        ).andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsByteArray();
    }

    @Test
    public void testDuplicateEmail() throws Exception {
        Coupon _coupon = new Coupon();
        _coupon.setEmail(System.currentTimeMillis() + "a@empal.com");

        byte[] returnValue2 = restCouponMockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(_coupon))
        ).andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsByteArray();
        coupon = objectMapper.readValue(returnValue2, Coupon.class);

        byte[] returnValue = restCouponMockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(coupon))
        ).andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsByteArray();
        System.out.println("" + new String(returnValue));
    }

    @Test
    public void testEmailValidation() throws Exception {
        Coupon _coupon = new Coupon();
        _coupon.setEmail(System.currentTimeMillis() + "empal.com");

        byte[] returnValue2 = restCouponMockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(_coupon))
        )
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsByteArray();
        System.out.println("" + new String(returnValue2));
    }

    private Coupon sendCreateRequest() {
        Coupon _coupon = new Coupon();
        _coupon.setEmail(System.currentTimeMillis() + "a@empal.com");
        return (Coupon) this.restTemplate.postForEntity("/api/coupons", _coupon, Coupon.class, new HashMap()).getBody();
    }

    private void sendDeleteRequest(String id) {
        couponRepository.delete(id);
        redisTemplate.boundZSetOps(Constants.REDIS_KEY_COUPON_SSET).remove(id);
    }
}