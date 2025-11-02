// Doctor.java
package com.example.doctorapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Specialty specialty;

    private String clinicLocation;
    private String contactInfo;
}
