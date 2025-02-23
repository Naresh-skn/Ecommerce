package com.springboot.ecommerce.service;

import com.springboot.ecommerce.dto.CategoryDTO;
import com.springboot.ecommerce.dto.CategoryResponse;
import com.springboot.ecommerce.entity.Category;

public interface CategoryService {


    CategoryDTO createCategory(CategoryDTO category);

    CategoryResponse getAllCategory(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder);

    CategoryDTO updateCategory(Long categoryId,CategoryDTO category);

    CategoryDTO deleteCategory(Long categoryId);
}



