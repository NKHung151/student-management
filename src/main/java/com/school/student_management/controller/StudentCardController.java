package com.school.student_management.controller;

import com.school.student_management.dto.StudentCardDTO;
import com.school.student_management.service.StudentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/student-cards")
@RequiredArgsConstructor
public class StudentCardController {

    private final StudentCardService studentCardService;

    @GetMapping
    public ResponseEntity<List<StudentCardDTO>> getAllCards(Authentication authentication) {
        boolean isParent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARENT"));
        if (isParent) {
            String username = authentication.getName();
            return ResponseEntity.ok(studentCardService.getCardsByParent(username));
        }
        return ResponseEntity.ok(studentCardService.getAllCards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentCardDTO> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(studentCardService.getCardById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<StudentCardDTO> getCardByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentCardService.getCardByStudent(studentId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDUCATION_OFFICER')")
    public ResponseEntity<StudentCardDTO> createCard(@Valid @RequestBody StudentCardDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentCardService.createCard(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDUCATION_OFFICER')")
    public ResponseEntity<StudentCardDTO> updateCard(@PathVariable Long id, @Valid @RequestBody StudentCardDTO dto) {
        return ResponseEntity.ok(studentCardService.updateCard(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        studentCardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
