package com.anmi.shoppingcart.rest;

import com.anmi.shoppingcart.rest.dto.AddUserDto;
import com.anmi.shoppingcart.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartUserController {

    private final CartService cartService;

    @PostMapping("/{cartId}/users")
    public void addUser(@PathVariable String cartId, @RequestBody AddUserDto addUserDto) {
        cartService.addUser(cartId, addUserDto);
    }

    @DeleteMapping("/{cartId}/users/{userId}")
    public void deleteUser(@PathVariable String cartId, @PathVariable String userId) {
        cartService.deleteUser(cartId, userId);
    }
}
