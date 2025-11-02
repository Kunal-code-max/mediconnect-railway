package com.example.doctorapp.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.doctorapp.entity.Doctor;
import com.example.doctorapp.entity.DoctorAvailability;
import com.example.doctorapp.repo.DoctorRepository;
import com.example.doctorapp.service.DoctorAvailabilityService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/doctor/availability")
public class DoctorAvailabilityController {

    @Autowired
    private DoctorAvailabilityService availabilityService;

    @Autowired
    private DoctorRepository doctorRepo;

    // Add availability for a doctor
    @PostMapping("/add")
    public DoctorAvailability addAvailability(
            @RequestParam Long doctorId,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime) {

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        LocalDate availableDate = LocalDate.parse(date);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        return availabilityService.addAvailability(doctor, availableDate, start, end);
    }

    // Get availability for a doctor on a specific date
    @GetMapping("/{doctorId}")
    public List<DoctorAvailability> getAvailability(
            @PathVariable Long doctorId,
            @RequestParam String date) {

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        LocalDate availableDate = LocalDate.parse(date);
        return availabilityService.getAvailableSlots(doctor, availableDate);
    }

    
}
