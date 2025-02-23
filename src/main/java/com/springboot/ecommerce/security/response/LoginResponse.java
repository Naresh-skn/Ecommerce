package com.springboot.ecommerce.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {

    private Long userId;

    private String username;

    private List<String> roles;

    private String jwtToken;

    public LoginResponse(Long id, String username, List<String> roles) {
        this.userId = id;
        this.username = username;
        this.roles = roles;
    }
}
