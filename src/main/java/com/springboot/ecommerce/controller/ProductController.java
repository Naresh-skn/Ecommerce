package com.springboot.ecommerce.controller;


import com.springboot.ecommerce.configuration.AppConstants;
import com.springboot.ecommerce.dto.ProductDTO;
import com.springboot.ecommerce.dto.ProductResponse;
import com.springboot.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("public/product")
    public ResponseEntity<ProductResponse> getAllProduct(
            @RequestParam(name = "pageNumber",required = false,defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false,defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy",required = false,defaultValue = "productName") String sortBy,
            @RequestParam(name = "sortOrder",required = false,defaultValue = AppConstants.SORT_ORDER) String sortOrder
    ){
        ProductResponse productResponse = productService.getAllProduct(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.ACCEPTED);
    }

    @PostMapping("admin/{categoryId}/product")
    public ResponseEntity<ProductDTO> createProduct(
            @PathVariable Long categoryId,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO product = productService.createProduct(categoryId,productDTO);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("public/{categoryId}/product")
    public ResponseEntity<ProductResponse> productByCategory(
            @PathVariable Long categoryId) {
        ProductResponse productResponse = productService.getProductByCategory(categoryId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("public/products/search/{keyword}")
    public ResponseEntity<ProductResponse> searchByProductKeyword(
            @PathVariable String keyword) {
        ProductResponse productResponse = productService.searchByProductKeyword(keyword);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PutMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedproductDTO = productService.updateProduct(productId,productDTO);
        return new ResponseEntity<>(updatedproductDTO, HttpStatus.OK);
    }

    @DeleteMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(
            @PathVariable Long productId){
        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }


}
