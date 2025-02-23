package com.springboot.ecommerce.controller;

import com.springboot.ecommerce.configuration.AppConstants;
import com.springboot.ecommerce.dto.CategoryDTO;
import com.springboot.ecommerce.dto.CategoryResponse;
import com.springboot.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("admin/category")
    public ResponseEntity<CategoryDTO> createNewCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @GetMapping("public/category")
    public ResponseEntity<CategoryResponse> getAllCategory(
            @RequestParam(name = "pageNumber",required = false,defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize",required = false,defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy",required = false,defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(name = "sortOrder",required = false,defaultValue = AppConstants.SORT_ORDER) String sortOrder
    ){
        CategoryResponse categories = categoryService.getAllCategory(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PutMapping("admin/category")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO,
                                                   @RequestParam Long categoryId ){
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryId,categoryDTO);
        return new ResponseEntity<>(updatedCategory, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("admin/category")
    public ResponseEntity<CategoryDTO> deleteCategory(@RequestParam Long categoryId ){
        CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategory, HttpStatus.ACCEPTED);
    }


}
