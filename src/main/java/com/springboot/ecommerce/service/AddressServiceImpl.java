package com.springboot.ecommerce.service;

import com.springboot.ecommerce.Utils.AuthUtils;
import com.springboot.ecommerce.dto.AddressDTO;
import com.springboot.ecommerce.entity.Address;
import com.springboot.ecommerce.entity.User;
import com.springboot.ecommerce.exception.APIException;
import com.springboot.ecommerce.exception.ResourceNotFoundException;
import com.springboot.ecommerce.repository.AddressRepository;
import com.springboot.ecommerce.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtils authUtils;

    @Autowired
    UserRepository userRepository;

    @Override
    public AddressDTO createNewAddress(AddressDTO addressDTO) {
        Address address = modelMapper.map(addressDTO,Address.class);
        User user = authUtils.loggedinUser();
        address.setUser(user);
        user.getAddresses().add(address);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream().
                    map(address -> modelMapper.map(address, AddressDTO.class)).toList();

    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new APIException("Address Not Found"));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses() {
        User user = authUtils.loggedinUser();
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {

        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address",  addressId,"addressId"));

        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setPincode(addressDTO.getPincode());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());
        Address updatedAddress = addressRepository.save(addressFromDatabase);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address",  addressId,"addressId"));


        User user = addressFromDatabase.getUser();
        if (user != null) {
            // Remove the address from the user's list
            user.getAddresses().remove(addressFromDatabase);
            // No need to explicitly call addressRepository.delete(addressFromDatabase)
            // Just save the user and the address will be removed automatically due to orphanRemoval
            userRepository.save(user);
        }

        // T0 ensure safely deleted
        addressRepository.delete(addressFromDatabase);

        return "Address deleted successfully with addressId: " + addressId;
    }
}
