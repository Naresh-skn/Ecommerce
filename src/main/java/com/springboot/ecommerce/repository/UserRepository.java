package com.springboot.ecommerce.repository;

import com.springboot.ecommerce.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(@NotBlank String username);

    boolean existsByEmail(@NotBlank String username);
}
