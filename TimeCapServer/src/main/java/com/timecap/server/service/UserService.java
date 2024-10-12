package com.timecap.server.service;

import com.timecap.server.dto.LoginDto;
import com.timecap.server.entity.UserEntity;
import com.timecap.server.repository.UserRepository;
import com.timecap.server.security.AesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private final Key key = AesUtil.generateKey();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService() throws Exception {
    }

    public UserEntity createUser(UserEntity user) {
        validateUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public UserEntity login(LoginDto loginDto) {
        UserEntity user = findByEmail(loginDto.getEmail());

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            System.out.println("Authorization failed");
        }
        return user;
    }
    public String createCapsule(String email, String capsule) {
        UserEntity user = findByEmail(email);
        String encryptedMessage = encryptCapsule(capsule);
        user.setCapsule(encryptedMessage);
        return userRepository.save(user).getCapsule();
    }

    public String viewCapsule(String email) {
        UserEntity user = findByEmail(email);
        String decryptedMessage = decryptCapsule(user.getCapsule());
        user.setCapsule(decryptedMessage);
        return decryptedMessage;
    }
    private void validateUser(UserEntity user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Invalid user data");
        }
    }
    private String encryptCapsule(String capsule) {
        try {
            return AesUtil.AesEncrypt(capsule, key);
        } catch (Exception e) {
            System.out.println(e);
        }
        return capsule;
    }
        private String decryptCapsule (String encryptedCapsule){
            try {
                return AesUtil.AesDecrypt(encryptedCapsule, key);
            } catch (Exception e) {
                System.out.println(e);
            }
            return encryptedCapsule;
        }
}
