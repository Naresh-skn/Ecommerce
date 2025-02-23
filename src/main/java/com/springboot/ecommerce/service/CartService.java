package com.springboot.ecommerce.service;

import com.springboot.ecommerce.dto.CartDTO;

public interface CartService {
    CartDTO addProductToCart(Long productId, Long quantity);

    CartDTO getCart();

    CartDTO updateProductCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long productId);
}
