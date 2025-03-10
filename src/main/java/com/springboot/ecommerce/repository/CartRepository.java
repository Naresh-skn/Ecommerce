package com.springboot.ecommerce.repository;

import com.springboot.ecommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Cart findByUser_UserId(Long userId);
}
