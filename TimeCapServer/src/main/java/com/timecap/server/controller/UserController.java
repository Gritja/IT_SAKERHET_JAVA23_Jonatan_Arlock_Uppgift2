package com.timecap.server.controller;

import com.timecap.server.security.JwtUtil;
import com.timecap.server.service.UserService;
import com.timecap.server.dto.LoginDto;
import com.timecap.server.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserEntity userEntity) {
        try {
            userService.createUser(userEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        try {
            UserEntity userEntity = userService.login(loginDto);
            String token = jwtUtil.generateToken(userEntity.getEmail());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    @PostMapping("/capsule/create")
    public ResponseEntity<String> createCapsule(@RequestHeader ("Authorization") String token, @RequestBody String capsule){
        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        userService.createCapsule(email, capsule);
        return ResponseEntity.ok("message sent");
    }
    @GetMapping("/capsule/view")
    public ResponseEntity<String> viewCapsule(@RequestHeader ("Authorization") String token ) {
        String email = jwtUtil.extractUsername(token.replace("Bearer " , ""));
        String capsules = userService.viewCapsule(email);
        return ResponseEntity.ok(capsules);
    }
}