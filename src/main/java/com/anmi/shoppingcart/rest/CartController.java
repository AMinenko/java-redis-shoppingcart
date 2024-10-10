package com.anmi.shoppingcart.rest;

import com.anmi.shoppingcart.rest.dto.CreateCartDto;
import com.anmi.shoppingcart.rest.dto.QtyDto;
import com.anmi.shoppingcart.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    String createCart(@RequestBody CreateCartDto cart) {
        UUID userId = UUID.randomUUID();
        return cartService.createCart(userId.toString(), cart);
    }

    @GetMapping("/{cartId}")
    Map<Object, Object> getCart(@PathVariable String cartId) {
        return cartService.getCart(cartId);
    }

    @PatchMapping("/{cartId}/items/{itemId}")
    public Map<String, Integer> updateItemQuantity(@PathVariable String cartId, @PathVariable String itemId, @RequestBody QtyDto qtyDto) {
        return cartService.updateItemQuantity(cartId, itemId, qtyDto);
    }


    @PatchMapping("/{cartId}/items/{itemId}/increment")
    @ResponseStatus(HttpStatus.OK)
    public void incrementItemQuantity(@PathVariable String cartId, @PathVariable String itemId, @RequestBody QtyDto qtyDto) {
        cartService.incrementItemQuantity(cartId, itemId, qtyDto);
    }

    @PatchMapping("/{cartId}/items/{itemId}/decrement")
    @ResponseStatus(HttpStatus.OK)
    public void decrementItemQuantity(@PathVariable String cartId, @PathVariable String itemId, @RequestBody QtyDto qtyDto) {
        cartService.decrementItemQuantity(cartId, itemId, qtyDto);
    }

}
