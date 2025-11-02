package com.example.doctorapp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.doctorapp.entity.LabAppointment;

public interface LabAppointmentRepository extends JpaRepository<LabAppointment, Long> {
    List<LabAppointment> findByPatientId(Long patientId);
    List<LabAppointment> findByReportSentFalse();
}
