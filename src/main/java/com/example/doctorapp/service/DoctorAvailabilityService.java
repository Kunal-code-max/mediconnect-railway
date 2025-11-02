package com.example.doctorapp.service;

import com.example.doctorapp.entity.*;
import com.example.doctorapp.repo.DoctorAvailabilityRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository repository;

    public DoctorAvailabilityService(DoctorAvailabilityRepository repository) {
        this.repository = repository;
    }

    public List<DoctorAvailability> getAvailableSlots(Doctor doctor, LocalDate date) {
        return repository.findByDoctorAndAvailableDate(doctor, date);
    }

    public DoctorAvailability addAvailability(Doctor doctor, LocalDate date, LocalTime start, LocalTime end) {
        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setAvailableDate(date);
        availability.setStartTime(start);
        availability.setEndTime(end);
        return repository.save(availability);
    }
}
