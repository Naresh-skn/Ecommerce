package com.springboot.ecommerce.Utils;

import com.springboot.ecommerce.entity.User;
import com.springboot.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class AuthUtils {

    @Autowired
    public UserRepository userRepository;

    public User loggedinUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        return userRepository.findByUsername(userName)
                .orElseThrow(()->new UsernameNotFoundException(" User Not found"));

    }

}
