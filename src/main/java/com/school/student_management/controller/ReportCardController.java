package com.school.student_management.controller;

import com.school.student_management.dto.ReportCardDTO;
import com.school.student_management.service.ReportCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/report-cards")
@RequiredArgsConstructor
public class ReportCardController {

    private final ReportCardService reportCardService;

    @GetMapping
    public ResponseEntity<List<ReportCardDTO>> getAllReportCards(Authentication authentication) {
        boolean isParent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARENT"));
        boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        if (isParent) {
            String username = authentication.getName();
            return ResponseEntity.ok(reportCardService.getReportCardsByParent(username));
        }
        if (isTeacher) {
            String username = authentication.getName();
            return ResponseEntity.ok(reportCardService.getReportCardsByTeacher(username));
        }
        return ResponseEntity.ok(reportCardService.getAllReportCards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportCardDTO> getReportCardById(@PathVariable Long id) {
        return ResponseEntity.ok(reportCardService.getReportCardById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ReportCardDTO>> getReportCardsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(reportCardService.getReportCardsByStudent(studentId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'EDUCATION_OFFICER')")
    public ResponseEntity<ReportCardDTO> createReportCard(@Valid @RequestBody ReportCardDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportCardService.createReportCard(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'EDUCATION_OFFICER', 'PARENT')")
    public ResponseEntity<ReportCardDTO> updateReportCard(@PathVariable Long id, @Valid @RequestBody ReportCardDTO dto) {
        return ResponseEntity.ok(reportCardService.updateReportCard(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReportCard(@PathVariable Long id) {
        reportCardService.deleteReportCard(id);
        return ResponseEntity.noContent().build();
    }
}
