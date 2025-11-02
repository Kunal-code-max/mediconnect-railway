package com.example.doctorapp.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.doctorapp.entity.Doctor;
import com.example.doctorapp.entity.Role;
import com.example.doctorapp.entity.Specialty;
import com.example.doctorapp.entity.User;
import com.example.doctorapp.repo.DoctorRepository;
import com.example.doctorapp.repo.UserRepository;

@Service
public class DoctorService {

    @Autowired private DoctorRepository doctorRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // Existing methods
    public Doctor createDoctor(String name, String email, String password, Specialty specialty,
                               String clinic, String contact) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(null, name, email, encodedPassword, Role.DOCTOR);
        userRepo.save(user);
        Doctor doctor = new Doctor(null, user, specialty, clinic, contact);
        return doctorRepo.save(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    public List<Doctor> getDoctorsBySpecialty(Specialty specialty) {
        return doctorRepo.findBySpecialty(specialty);
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepo.findById(id).orElse(null);
    }

    public Doctor findByEmail(String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return null;
        return doctorRepo.findByUser(user).orElse(null);
    }

    //  New helper method: get doctors by symptom
    public List<Doctor> getDoctorsBySymptom(String symptom) {
        Map<String, String> symptomToSpecialty = Map.of(
            "stomach", "GASTROENTEROLOGIST",
            "heart", "CARDIOLOGIST",
            "skin", "DERMATOLOGIST",
            "fever", "PHYSICIAN",
            "lung", "PULMONOLOGIST"
        );

        String specialtyStr = symptomToSpecialty.entrySet().stream()
                .filter(e -> symptom.toLowerCase().contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("GENERAL_PHYSICIAN");

        Specialty specialty = Specialty.valueOf(specialtyStr);

        return doctorRepo.findBySpecialty(specialty);
    }

    public boolean deleteDoctorById(Long id) {
    Optional<Doctor> doctor = doctorRepo.findById(id);
    if (doctor.isPresent()) {
        doctorRepo.deleteById(id);
        return true;
    } else {
        return false;
    }
}

}

