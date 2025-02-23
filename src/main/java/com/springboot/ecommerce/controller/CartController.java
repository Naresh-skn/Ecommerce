package com.springboot.ecommerce.controller;

import com.springboot.ecommerce.dto.CartDTO;
import com.springboot.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    CartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable Long productId,
            @PathVariable Long quantity
    ){
        CartDTO cartDTO = cartService.addProductToCart(productId,quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCart(){
        CartDTO cartDTO = cartService.getCart();
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateProductCart(
            @PathVariable Long productId,
            @PathVariable String operation
    ){
        Integer quantity = operation.equalsIgnoreCase("delete")?
                -1 : 1;
        CartDTO cartDTO = cartService.updateProductCart(productId,quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/product/{productId}")
    public ResponseEntity<?> deleteProductFromCart(
            @PathVariable Long productId
    ){
        String status = cartService.deleteProductFromCart(productId);
        return new ResponseEntity<String>(status, HttpStatus.OK);

    }


}
