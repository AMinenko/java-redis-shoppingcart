package com.anmi.shoppingcart.rest;

import com.anmi.shoppingcart.entity.Product;
import com.anmi.shoppingcart.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    @ResponseBody
    private List<Product> getListOfProducts(){
        return productRepository.findAll();
    }
}
