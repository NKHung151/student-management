package com.school.student_management.controller;

import com.school.student_management.dto.GradeSlipDTO;
import com.school.student_management.service.GradeSlipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grade-slips")
@RequiredArgsConstructor
public class GradeSlipController {

    private final GradeSlipService gradeSlipService;

    @GetMapping
    public ResponseEntity<List<GradeSlipDTO>> getAllGradeSlips(org.springframework.security.core.Authentication authentication) {
        if (authentication != null) {
            boolean isParent = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PARENT"));
            boolean isTeacher = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
            String username = authentication.getName();
            if (isParent) {
                return ResponseEntity.ok(gradeSlipService.getGradeSlipsByParent(username));
            }
            if (isTeacher) {
                return ResponseEntity.ok(gradeSlipService.getGradeSlipsByTeacher(username));
            }
        }
        return ResponseEntity.ok(gradeSlipService.getAllGradeSlips());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeSlipDTO> getGradeSlipById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeSlipService.getGradeSlipById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeSlipDTO>> getGradeSlipsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeSlipService.getGradeSlipsByStudent(studentId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'EDUCATION_OFFICER')")
    public ResponseEntity<GradeSlipDTO> createGradeSlip(@Valid @RequestBody GradeSlipDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeSlipService.createGradeSlip(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'EDUCATION_OFFICER')")
    public ResponseEntity<GradeSlipDTO> updateGradeSlip(@PathVariable Long id, @Valid @RequestBody GradeSlipDTO dto) {
        return ResponseEntity.ok(gradeSlipService.updateGradeSlip(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteGradeSlip(@PathVariable Long id) {
        gradeSlipService.deleteGradeSlip(id);
        return ResponseEntity.noContent().build();
    }
}
