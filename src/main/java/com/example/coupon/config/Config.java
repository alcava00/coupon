package com.example.coupon.config;

import com.example.coupon.service.RandomUniqueIdGenerator;
import com.example.coupon.service.UniqueIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by alcava00 on 2018. 3. 30..
 */
@Configuration
@EnableConfigurationProperties({CouponProperties.class})
public class Config {
    @Autowired
    private RedisProperties redisProperties;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

//    @Bean
//    public JedisConnectionFactory connectionFactory() {
//        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
//        connectionFactory.setHostName(redisProperties.getHost());
//        connectionFactory.setPort(redisProperties.getPort());
//        return connectionFactory;
//    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }


    @Bean
    public UniqueIdGenerator uniqueIdGenerator(CouponProperties couponProperties){
        return new RandomUniqueIdGenerator(couponProperties);
    }
}
