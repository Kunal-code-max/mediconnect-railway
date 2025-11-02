// LabController.java
package com.example.doctorapp.controller;

import com.example.doctorapp.entity.Role;
import com.example.doctorapp.entity.User;
import com.example.doctorapp.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/lab")
@CrossOrigin(origins = "*")
public class LabController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LabController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<User> labLogin(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User labUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lab user not found"));

        if (labUser.getRole() != Role.LAB) {
            throw new RuntimeException("Not a Lab account");
        }

        if (!passwordEncoder.matches(password, labUser.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return ResponseEntity.ok(labUser);
    }
}
