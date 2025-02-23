package com.springboot.ecommerce.service;

import com.springboot.ecommerce.dto.OrderRequest;
import com.springboot.ecommerce.dto.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest);
}
