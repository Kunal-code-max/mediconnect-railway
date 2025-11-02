package com.example.doctorapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.doctorapp.entity.Role;
import com.example.doctorapp.entity.User;
import com.example.doctorapp.repo.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create user
    public User createUser(String name, String email, String password, Role role) {
        if(userRepo.findByEmail(email).isPresent()){
            throw new RuntimeException("Email already exists!");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepo.save(user);
    }

    // Login
    public User login(String email, String rawPassword) {
        User user = userRepo.findByEmail(email).orElse(null);
        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        }
        throw new RuntimeException("Invalid credentials");
    }
}
