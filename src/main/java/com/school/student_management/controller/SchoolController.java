package com.school.student_management.controller;

import com.school.student_management.dto.SchoolDTO;
import com.school.student_management.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping
    public ResponseEntity<List<SchoolDTO>> getAllSchools() {
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolDTO> getSchoolById(@PathVariable Long id) {
        return ResponseEntity.ok(schoolService.getSchoolById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchoolDTO> createSchool(@Valid @RequestBody SchoolDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolService.createSchool(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchoolDTO> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolDTO dto) {
        return ResponseEntity.ok(schoolService.updateSchool(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchool(@PathVariable Long id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.noContent().build();
    }
}
