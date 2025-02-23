package com.springboot.ecommerce.dto;

import lombok.Data;

@Data
public class OrderRequest {

    private Long addressId;

    private String paymentMethod;

}
