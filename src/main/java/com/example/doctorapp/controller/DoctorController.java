package com.example.doctorapp.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doctorapp.entity.Appointment;
import com.example.doctorapp.entity.Doctor;
import com.example.doctorapp.entity.Role;
import com.example.doctorapp.entity.User;
import com.example.doctorapp.service.AppointmentService;
import com.example.doctorapp.service.DoctorService;

@RestController
@RequestMapping("/doctor")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired 
    private DoctorService doctorService;

    @Autowired 
    private AppointmentService appointmentService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Use BCrypt

    // Doctor login
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
    String email = body.get("email");
    String password = body.get("password");

    Doctor doctor = doctorService.findByEmail(email);

    if (doctor == null) {
        return ResponseEntity.status(401).body("Doctor not found with email: " + email);
    }

    // Role check to make sure it's a DOCTOR account
    if (doctor.getUser().getRole() != Role.DOCTOR) {
        return ResponseEntity.status(403).body("Not a doctor account");
    }

    if (!passwordEncoder.matches(password, doctor.getUser().getPassword())) {
        return ResponseEntity.status(401).body("Password mismatch");
    }

    // Create safe response DTO (don’t expose encoded password)
    Map<String, Object> response = new HashMap<>();
    response.put("id", doctor.getId());
    response.put("name", doctor.getUser().getName());
    response.put("email", doctor.getUser().getEmail());
    response.put("specialty", doctor.getSpecialty());
    response.put("clinicLocation", doctor.getClinicLocation());
    response.put("contactInfo", doctor.getContactInfo());
    response.put("role", doctor.getUser().getRole());

    return ResponseEntity.ok(response);
}



    // Doctor dashboard (appointments + patients)
    @GetMapping("/{doctorId}/dashboard")
    public ResponseEntity<?> dashboard(@PathVariable Long doctorId) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null) return ResponseEntity.notFound().build();

        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);

        // Collect unique patients from appointments
        Set<User> patients = new HashSet<>();
        for (Appointment appt : appointments) {
            patients.add(appt.getPatient());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("doctor", doctor);
        response.put("appointments", appointments);
        response.put("patients", patients);

        return ResponseEntity.ok(response);
    }
}
