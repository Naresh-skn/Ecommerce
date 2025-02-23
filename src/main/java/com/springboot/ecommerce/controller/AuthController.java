package com.springboot.ecommerce.controller;


import com.springboot.ecommerce.configuration.AppConstants;
import com.springboot.ecommerce.entity.AppRole;
import com.springboot.ecommerce.entity.Role;
import com.springboot.ecommerce.entity.User;
import com.springboot.ecommerce.repository.RoleRepository;
import com.springboot.ecommerce.repository.UserRepository;
import com.springboot.ecommerce.security.UserDetailsImpl;
import com.springboot.ecommerce.security.request.LoginRequest;
import com.springboot.ecommerce.security.request.SignupRequest;
import com.springboot.ecommerce.security.response.LoginResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private  Environment env;

    @Value("${spring.ecom.app.jwtCookieName}")
    private String jwtCookie;


    @GetMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.getUsername(),
                loginRequest.getPassword());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);
        LoginResponse response = null;
        ResponseCookie cookie=null;
//        String userAgent = request.getHeader("User-Agent");
//        String ipAddress = request.getRemoteAddr();  // Get IP Address
//
//        System.out.println(userAgent+" "+ipAddress);



        if(null!=authenticationResponse && authenticationResponse.isAuthenticated()){
            SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

                String secret = env.getProperty(AppConstants.JWT_SECRET_KEY,
                        AppConstants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                String jwt = Jwts.builder().issuer("Ecommerce").subject("JWT TOKEN")
                        .claim("username",authenticationResponse.getName())
                        .claim("authorities",authenticationResponse.getAuthorities().stream().
                                map((GrantedAuthority::getAuthority)).collect(Collectors.joining(",")))
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 30000000))
                        .signWith(secretKey).compact();



            UserDetailsImpl userDetails= UserDetailsImpl.build(userRepository.
                    findByUsername(((String)authenticationResponse.getPrincipal()))
                    .orElseThrow(()->new RuntimeException("No User Found")));

           String roles = authenticationResponse.getAuthorities().stream().map(
                    GrantedAuthority::getAuthority).collect(Collectors.joining(","));

            response= new LoginResponse(userDetails.getId(),
                    userDetails.getUsername(), Collections.singletonList(roles),
                    jwt);

            cookie = ResponseCookie.from(jwtCookie, jwt)
                    .path("/api")
                    .maxAge(24 * 60 * 60)
                    .httpOnly(false)
                    .secure(false)
                    .build();
        }


        assert cookie != null;
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body(response);

    }


    @PostMapping("/signup")
    public ResponseEntity<String> register(
            @Valid @RequestBody SignupRequest signupRequest){

        if(userRepository.existsByUsername(signupRequest.getUsername())){
            throw new RuntimeException("UserName already exists");
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));

        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if (authentication != null)
            return authentication.getName();
        else
            return "";
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails= UserDetailsImpl.build(userRepository.
                findByUsername(((String)authentication.getPrincipal()))
                .orElseThrow(()->new RuntimeException("No User Found")));

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(userDetails.getId(),
                userDetails.getUsername(), roles);

        return ResponseEntity.ok().body(response);
    }

//    @PostMapping("/signout")
//    public ResponseEntity<?> signoutUser(){
//        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
//        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
//                        cookie.toString())
//                .body(new MessageResponse("You've been signed out!"));
//    }

}
