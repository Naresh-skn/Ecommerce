package com.springboot.ecommerce.dto;

import com.springboot.ecommerce.entity.Address;
import com.springboot.ecommerce.entity.OrderItem;
import com.springboot.ecommerce.entity.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long orderId;

    private String payment; // has to implement

    private Double totalPrice;

    private Integer totalItems;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime orderDate;

    private LocalDateTime deliveryDate;

    private AddressDTO deliveryAddress;

    private List<OrderItemDTO> orderItems;

}
