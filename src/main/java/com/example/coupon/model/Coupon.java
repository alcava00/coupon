package com.example.coupon.model;

import com.example.coupon.Constants;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by alcava00 on 2018. 3. 26..
 */
@RedisHash(Constants.REDIS_KEY_COUPON)
public class Coupon {

    @Id
    private long id;

    @Indexed
    private String couponId;

    @NotNull
    @Email(message = "This email address is invalid")
    @Indexed
    private String email;

    private Date createdDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", couponId='" + couponId + '\'' +
                ", email='" + email + '\'' +
                ", createdDate=" + createdDate +

                '}';
    }
}
