package com.springboot.ecommerce.repository;

import com.springboot.ecommerce.entity.AppRole;
import com.springboot.ecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
