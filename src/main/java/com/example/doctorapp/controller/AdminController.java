// AdminController.java
package com.example.doctorapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doctorapp.entity.Doctor;
import com.example.doctorapp.entity.Role;
import com.example.doctorapp.entity.User;
import com.example.doctorapp.entity.Specialty;
import com.example.doctorapp.repo.UserRepository;
import com.example.doctorapp.service.DoctorService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired 
    private DoctorService doctorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // ===== Doctor Endpoints =====
    @PostMapping("/doctor")
    public Doctor addDoctor(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password"); 
        Specialty specialty = Specialty.valueOf(body.get("specialty").toUpperCase());
        String clinic = body.get("clinic");
        String contact = body.get("contact");

        return doctorService.createDoctor(name, email, password, specialty, clinic, contact);
    }

    @GetMapping("/doctors")
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/suggest-doctor")
    public List<Doctor> suggestDoctors(@RequestParam String symptom) {
        return doctorService.getDoctorsBySymptom(symptom);
    }

    @DeleteMapping("/doctor/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        boolean deleted = doctorService.deleteDoctorById(id);
        if (deleted) {
            return "Doctor with ID " + id + " deleted successfully.";
        } else {
            return "Doctor with ID " + id + " not found.";
        }
    }

    // ===== Lab Staff Endpoints =====
    @PostMapping("/create-lab")
    public User createLabUser(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User labUser = new User();
        labUser.setName(name);
        labUser.setEmail(email);
        labUser.setPassword(passwordEncoder.encode(password)); // encode password
        labUser.setRole(Role.LAB);

        return userRepository.save(labUser);
    }
}
