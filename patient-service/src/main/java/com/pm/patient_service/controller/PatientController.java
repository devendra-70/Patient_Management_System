package com.pm.patient_service.controller;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.dto.validators.CreatePatientValidationGroup;
import com.pm.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/patients")
@Tag(name="Patient", description="API for managing Patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }



    @GetMapping
    @Operation(summary = "Get Patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients(){
        List<PatientResponseDTO> patients=patientService.getPatients();
        return ResponseEntity.ok().body(patients);
    }



    @PostMapping
    @Operation(summary="Create a new Patient")
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);

        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary="Update a new Patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id,@Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO){

        PatientResponseDTO patientResponseDTO= patientService.updatePatient(id,patientRequestDTO);

        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary="Delete a Patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id){
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search Patients by name or email")
    public ResponseEntity<List<PatientResponseDTO>> searchPatients(
            @RequestParam("query") String query,
            @RequestParam("flag") String flag) {

        List<PatientResponseDTO> results = patientService.searchPatients(query, flag);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/update-by-email")
    @Operation(summary = "Update patient by email")
    public ResponseEntity<PatientResponseDTO> updatePatientByEmail(
            @RequestParam("email") String email,
            @RequestBody @Validated PatientRequestDTO dto) {

        PatientResponseDTO response = patientService.updatePatientByEmail(email, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-by-email")
    @Operation(summary = "Delete patient by email")
    public ResponseEntity<Void> deletePatientByEmail(@RequestParam("email") String email) {
        patientService.deletePatientByEmail(email);
        return ResponseEntity.noContent().build();
    }






}
