package com.springboot.ecommerce.service;

import com.springboot.ecommerce.dto.CategoryDTO;
import com.springboot.ecommerce.dto.CategoryResponse;
import com.springboot.ecommerce.entity.Category;
import com.springboot.ecommerce.exception.APIException;
import com.springboot.ecommerce.exception.ResourceNotFoundException;
import com.springboot.ecommerce.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO,Category.class);
        Category existingCategory = categoryRepository
                .findByCategoryName(category.getCategoryName());
        if(existingCategory!=null)
            throw new APIException("Category Name Already Exists");
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryDTO.class);
    }

    public CategoryResponse getAllCategory(Integer pageNumber,Integer pageSize,
    String sortBy,String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                                Sort.by(sortBy).ascending()
                                :Sort.by(sortBy).descending();
        if(!isValidField(Category.class,sortBy))
            throw new APIException("Not a valid field: "+sortBy);
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty())
            throw new APIException("No categories found");
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category,CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategories(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return  categoryResponse;
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId,CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO,Category.class);
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Category", categoryId, "resourceNotFound"));
        Category existingCategory = categoryRepository
                .findByCategoryName(category.getCategoryName());
        if(existingCategory!=null)
            throw new APIException("Category Name Already Exists");
        savedCategory.setCategoryName(category.getCategoryName());
        Category savedeCategory = categoryRepository.save(savedCategory);
        return modelMapper.map(savedCategory,CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException
                        ("Category", categoryId, "resourceNotFound"));

        categoryRepository.delete(savedCategory);
        return modelMapper.map(savedCategory,CategoryDTO.class);
    }

    public boolean isValidField(Class<Category> categoryClass,String fieldName){
        for (Field field: categoryClass.getDeclaredFields()){
            if(field.getName().equals(fieldName))
                return true;
        }
        return false;
    }

}
