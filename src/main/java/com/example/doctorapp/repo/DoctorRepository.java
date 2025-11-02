package com.example.doctorapp.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.doctorapp.entity.Doctor;
import com.example.doctorapp.entity.Specialty;
import com.example.doctorapp.entity.User;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialty(Specialty specialty);
    Optional<Doctor> findByUser(User user);
}
