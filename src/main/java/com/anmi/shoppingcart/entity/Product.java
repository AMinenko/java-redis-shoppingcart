package com.anmi.shoppingcart.entity;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "products")
@Data
public class Product {

    @Id
    private String id;

    private String name;
    @Field(name = "main_category")
    private String mainCategory;
    @Field(name = "sub_category")
    private String subCategory;
    @Field(name = "image")
    private String imageUrl;
    @Field(name = "product")
    private String productLink;
    private String ratings;
    @Field(name = "no_of_ratings")
    private String numberOfRatings;
    @Field(name = "discount_price")
    private String discountPrice;
    @Column(name = "actual_price")
    private String actualPrice;

}