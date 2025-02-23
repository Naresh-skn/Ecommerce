package com.springboot.ecommerce.configuration;

import com.springboot.ecommerce.dto.OrderItemDTO;
import com.springboot.ecommerce.entity.OrderItem;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Config {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(OrderItem.class, OrderItemDTO.class).addMappings(mapper -> {
            mapper.map(OrderItem::getPrice, OrderItemDTO::setOrderedProductPrice);
        });

        return modelMapper;
    }

}
