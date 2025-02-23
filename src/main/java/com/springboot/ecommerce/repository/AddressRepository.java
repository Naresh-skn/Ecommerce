package com.springboot.ecommerce.repository;

import com.springboot.ecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Long>{


}
