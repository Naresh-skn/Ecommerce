package com.springboot.ecommerce.service;

import com.springboot.ecommerce.Utils.AuthUtils;
import com.springboot.ecommerce.dto.*;
import com.springboot.ecommerce.entity.*;
import com.springboot.ecommerce.exception.APIException;
import com.springboot.ecommerce.exception.ResourceNotFoundException;
import com.springboot.ecommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final AuthUtils authUtils;

    private final AddressRepository addressRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final OrderItemRepository orderItemRepository;

    private final ModelMapper modelMapper;

    private final ProductRepository productRepository;


    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        User user = authUtils.loggedinUser();
        Address address = addressRepository.findById(orderRequest.getAddressId())
                .orElseThrow(()->new ResourceNotFoundException("Address ",
                        orderRequest.getAddressId(), "Not Found"));
        Cart cart = cartRepository.findByUser_UserId(user.getUserId());
        List<CartItem> cartItems = cart.getCartItems();

//         Validate stock and compute total price
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new APIException("Product " + product.getProductId() + " has insufficient stock.");
            }
        }

        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now();

        order.setOrderItems(orderItems);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(cart.getTotalPrice());
        order.setDeliveryAddress(address);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setDeliveryDate(localDateTime.plusDays(2));
        order.setUser(user);
        order.setPayment("COMPLETED");
        order.setTotalItems(cartItems.size());


        for(CartItem cartItem : cartItems){

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setPrice(cartItem.getProductPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrder(order);
            orderItems.add(orderItem);

            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity()-cartItem.getQuantity());
            cartItem.setProduct(product);
            productRepository.save(product);
        }

        orderItemRepository.saveAll(orderItems);

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cart.getCartItems().clear();
        cart.setTotalPrice(0.0);

        return modelMapper.map(savedOrder,OrderResponse.class);
    }
}
