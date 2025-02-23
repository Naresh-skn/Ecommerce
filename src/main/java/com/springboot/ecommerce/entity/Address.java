package com.springboot.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long addressId;

    @NotBlank
    @Size(min = 3, message = "Building name must be atleast 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 3, message = "Street name must be atleast 3 characters")
    private String street;

    @NotBlank
    @Size(min = 3, message = "city name must be atleast 3 characters")
    private String city;

    @NotBlank
    @Size(min = 3, message = "state name must be atleast 3 characters")
    private String state;

    @NotNull(message = "Pin code cannot be null")
    @Digits(integer = 6, fraction = 0, message = "Pin code must be exactly 6 digits")
    private Integer pincode;


    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
