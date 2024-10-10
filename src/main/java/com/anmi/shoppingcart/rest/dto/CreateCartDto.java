package com.anmi.shoppingcart.rest.dto;

import java.util.List;
public record CreateCartDto (List<AddProductDto> products){
}

