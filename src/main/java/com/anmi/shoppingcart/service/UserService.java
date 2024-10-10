package com.anmi.shoppingcart.service;

import com.anmi.shoppingcart.enums.RedisKeyUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@AllArgsConstructor
public class UserService {

    private final RedisTemplate<String, String> redisTemplate;

    public void addUser(String cartId, String userId) {
        String userSetKey = RedisKeyUtil.getCartUserKey(cartId);
        redisTemplate.opsForSet().add(userSetKey, userId);
    }

    public void removeUser(String cartId, String userId) {
        String userSetKey = RedisKeyUtil.getCartUserKey(cartId);
        redisTemplate.opsForSet().remove(userSetKey, userId);
    }

    public boolean userBelongsToCart(String cartId, String userId) {
        String userSetKey = RedisKeyUtil.getCartUserKey(cartId);
        return redisTemplate.opsForSet().isMember(userSetKey, userId);
    }
}
