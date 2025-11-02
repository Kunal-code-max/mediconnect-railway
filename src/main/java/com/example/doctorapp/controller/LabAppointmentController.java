package com.example.doctorapp.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.doctorapp.entity.LabAppointment;
import com.example.doctorapp.entity.User;
import com.example.doctorapp.repo.UserRepository;
import com.example.doctorapp.service.LabAppointmentService;

@RestController
@RequestMapping("/lab")
@CrossOrigin(origins = "http://localhost:5173") 
public class LabAppointmentController {

    private final LabAppointmentService service;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public LabAppointmentController(LabAppointmentService service, UserRepository userRepository, JavaMailSender mailSender) {
        this.service = service;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    /**
     * Inner class to represent booking request.
     */
    public static class LabAppointmentRequest {
        private Long patientId;
        private String testType;  // e.g., "Blood Test"
        private LocalDate date;
        private LocalTime timeSlot;

        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }

        public String getTestType() { return testType; }
        public void setTestType(String testType) { this.testType = testType; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public LocalTime getTimeSlot() { return timeSlot; }
        public void setTimeSlot(LocalTime timeSlot) { this.timeSlot = timeSlot; }
    }

    /**
     * Book a new lab appointment
     */
    @PostMapping("/book")
    public ResponseEntity<LabAppointment> bookLabAppointment(@RequestBody LabAppointmentRequest request) {
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LabAppointment appointment = new LabAppointment();
        appointment.setPatient(patient);
        appointment.setTestType(request.getTestType());
        appointment.setDate(request.getDate());
        appointment.setTimeSlot(request.getTimeSlot());

        LabAppointment saved = service.bookAppointment(appointment);
        return ResponseEntity.ok(saved);
    }

    /**
     * Get all lab appointments for a specific patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabAppointment>> getPatientAppointments(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.getPatientAppointments(patientId));
    }

    /**
     * Get all pending lab appointments (report not sent yet)
     */
    @GetMapping("/pending")
    public ResponseEntity<List<LabAppointment>> getPendingAppointments() {
        return ResponseEntity.ok(service.getPendingLabAppointments());
    }

    /**
     * Send report for a lab appointment and send email
     */
    @PostMapping("/send-report/{appointmentId}")
    public ResponseEntity<LabAppointment> sendReport(
            @PathVariable Long appointmentId,
            @RequestParam String reportUrl
    ) {
        LabAppointment updated = service.sendReport(appointmentId, reportUrl);

        // Send email to patient
        User patient = updated.getPatient();
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(patient.getEmail());
            message.setSubject("Your Lab Report is Ready");
            message.setText("Hello " + patient.getName() + ",\n\n" +
                    "Your lab report for " + updated.getTestType() + " is ready.\n" +
                    "You can view it here: " + reportUrl + "\n\n" +
                    "Thank you,\nDoctorApp Lab");

            try {
                mailSender.send(message);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to send email to " + patient.getEmail());
            }
        }

        return ResponseEntity.ok(updated);
    }
}
