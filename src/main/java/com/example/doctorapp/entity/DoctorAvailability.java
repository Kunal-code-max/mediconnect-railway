// DoctorAvailability.java
package com.example.doctorapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name="doctor_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="doctor_id")
    private Doctor doctor;

    private LocalDate availableDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
