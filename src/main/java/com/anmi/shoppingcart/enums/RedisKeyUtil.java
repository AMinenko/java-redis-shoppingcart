package com.anmi.shoppingcart.enums;

public final class RedisKeyUtil {
    public static final String shoppingCartKey = "cart:%s";
    public static final String shoppingCartUsersKey = "cart:%s:users";

    public static String getCartKey(String cartId) {
        return shoppingCartKey.formatted(cartId);
    }

    public static String getCartUserKey(String cartId) {
        return shoppingCartUsersKey.formatted(cartId);
    }
}
