package com.springboot.ecommerce.controller;

import com.springboot.ecommerce.dto.AddressDTO;
import com.springboot.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/user/address")
    public ResponseEntity<AddressDTO> createNewAddress(
            @Valid @RequestBody AddressDTO addressDTO){
        AddressDTO responseAddress = addressService.createNewAddress(addressDTO);
        return new ResponseEntity<>(responseAddress,HttpStatus.CREATED);
    }

    @GetMapping("/admin/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses(){
        List<AddressDTO> addressList = addressService.getAllAddresses();
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    @GetMapping("/user/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(
            @PathVariable Long addressId
    ){
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(){
        List<AddressDTO> addressList = addressService.getUserAddresses();
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    @PutMapping("/user/address/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId
            , @RequestBody AddressDTO addressDTO){
        AddressDTO updatedAddress = addressService.updateAddress(addressId, addressDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
