package com.example.doctorapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.doctorapp.entity.LabAppointment;
import com.example.doctorapp.repo.LabAppointmentRepository;

@Service
public class LabAppointmentService {

    private final LabAppointmentRepository repository;

    public LabAppointmentService(LabAppointmentRepository repository) {
        this.repository = repository;
    }

    public LabAppointment bookAppointment(LabAppointment appointment) {
        return repository.save(appointment);
    }

    public List<LabAppointment> getPatientAppointments(Long patientId) {
        return repository.findByPatientId(patientId);
    }

    public List<LabAppointment> getPendingLabAppointments() {
        return repository.findByReportSentFalse();
    }

    public LabAppointment sendReport(Long appointmentId, String reportUrl) {
        LabAppointment appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setReportUrl(reportUrl);
        appointment.setReportSent(true);
        return repository.save(appointment);
    }
}
