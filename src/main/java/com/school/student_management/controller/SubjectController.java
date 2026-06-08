package com.school.student_management.controller;

import com.school.student_management.dto.SubjectDTO;
import com.school.student_management.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(subjectService.getSubjectsByTeacher(teacherId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDUCATION_OFFICER')")
    public ResponseEntity<SubjectDTO> createSubject(@Valid @RequestBody SubjectDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.createSubject(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDUCATION_OFFICER')")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(subjectService.updateSubject(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
