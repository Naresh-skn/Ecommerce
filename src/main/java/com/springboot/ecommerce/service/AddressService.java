package com.springboot.ecommerce.service;

import com.springboot.ecommerce.dto.AddressDTO;
import com.springboot.ecommerce.entity.User;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createNewAddress(@Valid AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses();

    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
