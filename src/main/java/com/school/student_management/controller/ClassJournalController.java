package com.school.student_management.controller;

import com.school.student_management.dto.ClassJournalDTO;
import com.school.student_management.service.ClassJournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-journals")
@RequiredArgsConstructor
public class ClassJournalController {

    private final ClassJournalService classJournalService;

    @GetMapping
    public ResponseEntity<List<ClassJournalDTO>> getAllJournals(org.springframework.security.core.Authentication authentication) {
        if (authentication != null) {
            boolean isTeacher = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
            if (isTeacher) {
                return ResponseEntity.ok(classJournalService.getJournalsByTeacher(authentication.getName()));
            }
        }
        return ResponseEntity.ok(classJournalService.getAllJournals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassJournalDTO> getJournalById(@PathVariable Long id) {
        return ResponseEntity.ok(classJournalService.getJournalById(id));
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<ClassJournalDTO>> getJournalsByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(classJournalService.getJournalsByClassroom(classroomId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'EDUCATION_OFFICER')")
    public ResponseEntity<ClassJournalDTO> createJournal(@Valid @RequestBody ClassJournalDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classJournalService.createJournal(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'EDUCATION_OFFICER')")
    public ResponseEntity<ClassJournalDTO> updateJournal(@PathVariable Long id, @Valid @RequestBody ClassJournalDTO dto) {
        return ResponseEntity.ok(classJournalService.updateJournal(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteJournal(@PathVariable Long id) {
        classJournalService.deleteJournal(id);
        return ResponseEntity.noContent().build();
    }
}
