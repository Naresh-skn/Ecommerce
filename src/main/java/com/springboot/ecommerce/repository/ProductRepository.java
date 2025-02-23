package com.springboot.ecommerce.repository;

import com.springboot.ecommerce.entity.Category;
import com.springboot.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByCategoryOrderByPriceAsc(Category category);

    List<Product> findByProductNameLikeIgnoreCase(String keyword);

    Product findByProductName(String productName);
}
