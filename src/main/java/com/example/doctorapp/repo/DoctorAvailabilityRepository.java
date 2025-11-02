package com.example.doctorapp.repo;

import com.example.doctorapp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctorAndAvailableDate(Doctor doctor, LocalDate date);
}
