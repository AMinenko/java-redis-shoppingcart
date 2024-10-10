package com.anmi.shoppingcart.service;

import com.anmi.shoppingcart.enums.RedisKeyUtil;
import com.anmi.shoppingcart.rest.dto.AddUserDto;
import com.anmi.shoppingcart.rest.dto.CreateCartDto;
import com.anmi.shoppingcart.rest.dto.QtyDto;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CartService {

    private final RedisTemplate<String, String> redisTemplate;

    public String createCart(String userId, CreateCartDto cartDto) {
        String cartId = UUID.randomUUID().toString();

        Map<String, String> cartPayload = new HashMap<>();
        Map<String, String> products = cartDto.products().stream()
                .collect(Collectors.toMap(product -> product.id(), el -> el.qty()));
        cartPayload.put("ownerId", userId.toString());
        cartPayload.putAll(products);

        redisTemplate.opsForHash().putAll(RedisKeyUtil.getCartKey(cartId), cartPayload);
        return cartId;
    }

    public Map<Object, Object> getCart(String cartId) {
        String cartKey = RedisKeyUtil.getCartKey(cartId);
        return redisTemplate.opsForHash().entries(cartKey);
    }

    /**
     * Updates the quantity of an item in the shopping cart.
     * <p>
     * This method uses optimistic locking to ensure that the item
     * quantity is updated only if the quantity seen by the user
     * matches the current quantity in the cart. If another user
     * modifies the quantity before this operation, a conflict
     * exception will be thrown.
     *
     * @param cartId The ID of the shopping cart.
     * @param itemId The ID of the item to be updated.
     * @param qtyDto The DTO containing the new quantity and the previous quantity
     *               as seen by the user.
     * @return true if the quantity was successfully updated; otherwise, it throws an exception.
     * @throws IllegalArgumentException if the item quantity has been modified
     *                                  by another user since it was last fetched
     *                                  or if the current quantity does not match
     *                                  the expected previous quantity.
     */
    public Map<String, Integer> updateItemQuantity(String cartId, String itemId, QtyDto qtyDto) {
        String cartKey = RedisKeyUtil.getCartKey(cartId);
        redisTemplate.watch(cartKey);

        try {
            String currentQty = (String) redisTemplate.opsForHash().get(cartKey, itemId);
            if (currentQty != null && !currentQty.equals(qtyDto.currQty().toString())) {
                throw new IllegalArgumentException("Stale state detected! Another user has updated the item.");
            }
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                connection.multi();
                connection.hashCommands().hSet(cartKey.getBytes(),
                        itemId.getBytes(),
                        qtyDto.newQty().toString().getBytes()
                );
                return connection.exec();
            });
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Property has been updated, please refresh the page");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updating the product qty. please try again");
        } finally {
            redisTemplate.unwatch();
        }
        return Map.of(itemId, qtyDto.newQty());
    }

    public void incrementItemQuantity(String cartId, String itemId, QtyDto qtyDto) {
        String cartKey = RedisKeyUtil.getCartKey(cartId);
        redisTemplate.watch(cartKey);
        String currentQty = (String) redisTemplate.opsForHash().get(cartKey, itemId);
        if (Integer.parseInt(currentQty) == qtyDto.currQty()) {
            return;
        }
        try {
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                connection.multi();
                connection.hashCommands().hIncrBy(cartKey.getBytes(),
                        itemId.getBytes(),
                        1
                );
                return connection.exec();
            });
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updating the product qty. please try again");
        } finally {
            redisTemplate.unwatch();
        }

    }

    public void decrementItemQuantity(String cartId, String itemId, QtyDto qtyDto) {
        String cartKey = RedisKeyUtil.getCartKey(cartId);
        redisTemplate.watch(cartKey);
        String currentQty = (String) redisTemplate.opsForHash().get(cartKey, itemId);
        if (Integer.parseInt(currentQty) == qtyDto.currQty()) {
            return;
        }
        try {
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                connection.multi();
                connection.hashCommands().hIncrBy(cartKey.getBytes(),
                        itemId.getBytes(),
                        -1
                );
                return connection.exec();
            });
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updating the product qty. please try again");
        } finally {
            redisTemplate.unwatch();
        }
    }

    public Long addUser(String cartId, AddUserDto addUserDto) {
        String cartUserKey = RedisKeyUtil.getCartUserKey(cartId);
        return redisTemplate.opsForSet().add(cartUserKey, addUserDto.userId());
    }

    public Long deleteUser(String cartId, String userId) {
        String cartUserKey = RedisKeyUtil.getCartUserKey(cartId);
        return redisTemplate.opsForSet().remove(cartUserKey, userId);
    }
}
