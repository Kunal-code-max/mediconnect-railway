package com.example.doctorapp.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.doctorapp.entity.Appointment;
import com.example.doctorapp.entity.Doctor;
import com.example.doctorapp.entity.Role;
import com.example.doctorapp.entity.Specialty;
import com.example.doctorapp.entity.User;
import com.example.doctorapp.service.AppointmentService;
import com.example.doctorapp.service.DoctorService;
import com.example.doctorapp.service.UserService;

@RestController
@RequestMapping("/patient")
@CrossOrigin(origins = "*") 
public class PatientController {

    @Autowired private DoctorService doctorService;
    @Autowired private AppointmentService appointmentService;
    @Autowired private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

  
    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.createUser(user.getName(), user.getEmail(), user.getPassword(), Role.PATIENT);
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User user) {
    User loggedIn = userService.login(user.getEmail(), user.getPassword());
    if (loggedIn.getRole() != Role.PATIENT) {
        return ResponseEntity.status(401).body("Not a patient account");
    }
    return ResponseEntity.ok(loggedIn);
}


    
    @GetMapping("/doctors")
    public List<Doctor> getDoctors(@RequestParam(required = false) String specialty) {
        if (specialty != null) {
            return doctorService.getDoctorsBySpecialty(Specialty.valueOf(specialty.toUpperCase()));
        }
        return doctorService.getAllDoctors();
    }

    
    @PostMapping("/appointments")
    public Appointment bookAppointment(@RequestBody Map<String, String> body) {
        Long doctorId = Long.parseLong(body.get("doctorId"));
        Long patientId = Long.parseLong(body.get("patientId")); // logged-in patient
        String patientName = body.get("patientName");
        String contact = body.get("contact");
        LocalDate date = LocalDate.parse(body.get("date"));
        LocalTime time = LocalTime.parse(body.get("time"));
        String reason = body.get("reason");

        return appointmentService.bookAppointment(doctorId, patientId, patientName, contact, date, time, reason);
    }

    
    @GetMapping("/{patientId}/appointments")
    public List<Appointment> getAppointments(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatientId(patientId);
    }
}
