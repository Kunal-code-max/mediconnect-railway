package com.example.doctorapp.repo;
import com.example.doctorapp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
     boolean existsByEmail(String email);
}
