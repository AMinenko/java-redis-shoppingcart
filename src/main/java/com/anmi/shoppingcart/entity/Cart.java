package com.anmi.shoppingcart.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@Data
@RedisHash
@Builder
public class Cart {
    @Id
    private String id;

    private String owner;

    private Map<String,String> productQtyMap;
}
