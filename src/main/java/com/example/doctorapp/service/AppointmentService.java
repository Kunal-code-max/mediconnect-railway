package com.example.doctorapp.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.doctorapp.entity.*;
import com.example.doctorapp.repo.*;

@Service
public class AppointmentService {

    @Autowired private AppointmentRepository appointmentRepo;
    @Autowired private DoctorRepository doctorRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private DoctorAvailabilityRepository availabilityRepo;

    public Appointment bookAppointment(Long doctorId, Long patientId, String patientName,
                                       String patientContact, LocalDate date,
                                       LocalTime time, String reason) {

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        User patient = userRepo.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        // 1️⃣ Check appointment is not in the past
        if(date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book appointment in the past");
        }

        // 2️⃣ Check if time is within doctor's availability
        List<DoctorAvailability> slots = availabilityRepo.findByDoctorAndAvailableDate(doctor, date);
        boolean isValidTime = slots.stream().anyMatch(slot ->
            !time.isBefore(slot.getStartTime()) && !time.isAfter(slot.getEndTime())
        );
        if(!isValidTime) {
            throw new IllegalArgumentException("Selected time is outside doctor's available slots");
        }

        // 3️⃣ Check if time is already booked
        List<Appointment> booked = appointmentRepo.findByDoctorIdAndAppointmentDate(doctorId, date);
        List<LocalTime> bookedTimes = booked.stream()
                .map(Appointment::getAppointmentTime)
                .collect(Collectors.toList());
        if(bookedTimes.contains(time)) {
            throw new IllegalArgumentException("Selected time is already booked");
        }

        // 4️⃣ Save appointment
        Appointment appt = new Appointment();
        appt.setDoctor(doctor);
        appt.setPatient(patient);
        appt.setPatientName(patientName);
        appt.setPatientContact(patientContact);
        appt.setAppointmentDate(date);
        appt.setAppointmentTime(time);
        appt.setReason(reason);
        appt.setStatus(AppointmentStatus.PENDING);

        return appointmentRepo.save(appt);
    }

    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepo.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepo.findByPatientId(patientId);
    }
}
