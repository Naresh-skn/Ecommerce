package com.springboot.ecommerce.repository;

import com.springboot.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    CartItem findByCart_CartIdAndProduct_ProductId(Long cartId, Long productId);
}
