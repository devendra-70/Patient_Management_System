package com.pm.patient_service.repository;

import com.pm.patient_service.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email,UUID id);
    // NEW: Case-insensitive partial match for name or email
    List<Patient> findByNameContainingIgnoreCase(String name);
    List<Patient> findByEmailContainingIgnoreCase(String email);
    Optional<Patient> findByEmail(String email);


}
