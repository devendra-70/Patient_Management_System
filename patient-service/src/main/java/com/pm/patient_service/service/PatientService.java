package com.pm.patient_service.service;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.exception.EmailAlreadyExistsException;
import com.pm.patient_service.exception.PatientNotFoundException;
import com.pm.patient_service.grpc.BillingServiceGrpcClient;
import com.pm.patient_service.kafka.KafkaProducer;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient,KafkaProducer kafkaProducer){
        this.patientRepository=patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer=kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients=patientRepository.findAll();

        List<PatientResponseDTO> patientResponseDTOs =
                patients.stream().map(
                        patient -> PatientMapper.toDTO(patient)
                ).toList();

        return patientResponseDTOs;

    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists: " + patientRequestDTO.getEmail());
        }

        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(
                newPatient.getId().toString(),
                newPatient.getName(),
                newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient, PatientEvent.EventType.ADDED); // ✅ FIXED

        return PatientMapper.toDTO(newPatient);
    }


    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("A patient with this email already exists: " + patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        kafkaProducer.sendEvent(updatedPatient, PatientEvent.EventType.UPDATED); // ✅ NEW!

        return PatientMapper.toDTO(updatedPatient);
    }


    public void deletePatient(UUID id){
        patientRepository.deleteById(id);
    }

    public List<PatientResponseDTO> searchPatients(String query, String flag) {
        List<Patient> patients;

        if ("name".equalsIgnoreCase(flag)) {
            patients = patientRepository.findByNameContainingIgnoreCase(query);
        } else if ("email".equalsIgnoreCase(flag)) {
            patients = patientRepository.findByEmailContainingIgnoreCase(query);
        } else {
            throw new IllegalArgumentException("Invalid flag: must be 'name' or 'email'");
        }

        return patients.stream()
                .map(PatientMapper::toDTO)
                .toList();
    }

    public PatientResponseDTO updatePatientByEmail(String email, PatientRequestDTO dto) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with email: " + email));

        // Update name only if provided
        if (dto.getName() != null && !dto.getName().isBlank()) {
            patient.setName(dto.getName());
        }

        // Update address only if provided
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            patient.setAddress(dto.getAddress());
        }

        // Update date of birth only if provided
        if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().isBlank()) {
            patient.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        }

        // If email changed and is provided
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(patient.getEmail())) {
            if (patientRepository.existsByEmail(dto.getEmail())) {
                throw new EmailAlreadyExistsException("A patient with this email already exists: " + dto.getEmail());
            }
            patient.setEmail(dto.getEmail());
        }

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }


    public void deletePatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with email: " + email));
        patientRepository.deleteById(patient.getId());
    }





}

