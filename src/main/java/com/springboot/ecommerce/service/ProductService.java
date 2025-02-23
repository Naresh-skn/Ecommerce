package com.springboot.ecommerce.service;

import com.springboot.ecommerce.dto.ProductDTO;
import com.springboot.ecommerce.dto.ProductResponse;

public interface ProductService {

    ProductDTO createProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getProductByCategory(Long categoryId);

    ProductResponse searchByProductKeyword(String keyword);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO deleteProduct(Long productId);

    ProductResponse getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
