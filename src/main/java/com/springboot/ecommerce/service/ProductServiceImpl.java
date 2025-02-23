package com.springboot.ecommerce.service;

import com.springboot.ecommerce.dto.ProductDTO;
import com.springboot.ecommerce.dto.ProductResponse;
import com.springboot.ecommerce.entity.Category;
import com.springboot.ecommerce.entity.Product;
import com.springboot.ecommerce.exception.APIException;
import com.springboot.ecommerce.exception.ResourceNotFoundException;
import com.springboot.ecommerce.repository.CategoryRepository;
import com.springboot.ecommerce.repository.ProductRepository;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductResponse getAllProduct(Integer pageNumber, Integer pageSize,
                                         String sortBy, String sortOrder) {
        if(!isValidField(Product.class,sortBy))
            throw new APIException("Not a valid field: "+sortBy);
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productByPagination = productRepository.findAll(pageDetails);
        List<Product> products = productByPagination.getContent();
        if(products.isEmpty())
            throw new APIException("No Products found");
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO createProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Category", categoryId, "resourceNotFound"));
        Product existingProduct = productRepository
                .findByProductName(productDTO.getProductName());
        if(existingProduct!=null)
            throw new APIException("Product Name Already Exists");
        Product product = modelMapper.map(productDTO,Product.class);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);

    }

    @Override
    public ProductResponse getProductByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Category", categoryId, "resourceNotFound"));
        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        if(products.isEmpty())
            throw new APIException("No Product found for category: "+categoryId);

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchByProductKeyword(String keyword) {
        List<Product> products = productRepository
                .findByProductNameLikeIgnoreCase("%"+keyword+"%");
        if(products.isEmpty())
            throw new APIException("No Products Found");
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;

    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product",productId,"Not Found"));
        product.setDescription(productDTO.getDescription());
        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount());
        product.setImage(productDTO.getImage());
        product.setQuantity(productDTO.getQuantity());
        productRepository.save(product);
        return modelMapper.map(product,ProductDTO.class);

    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product",productId,"Not Found"));
        productRepository.delete(product);
        return modelMapper.map(product,ProductDTO.class);
    }

    public boolean isValidField(Class<Product> productClass,String fieldName){
        for (Field field: productClass.getDeclaredFields()){
            if(field.getName().equals(fieldName))
                return true;
        }
        return false;
    }




}
