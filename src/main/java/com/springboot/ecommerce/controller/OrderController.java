package com.springboot.ecommerce.controller;

import com.springboot.ecommerce.dto.OrderRequest;
import com.springboot.ecommerce.dto.OrderResponse;
import com.springboot.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order/users")
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestBody OrderRequest orderRequest
    ){
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

}
