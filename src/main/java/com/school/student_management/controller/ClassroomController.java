package com.school.student_management.controller;

import com.school.student_management.dto.ClassroomDTO;
import com.school.student_management.service.ClassroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping
    public ResponseEntity<List<ClassroomDTO>> getAllClassrooms(org.springframework.security.core.Authentication authentication) {
        if (authentication != null) {
            boolean isTeacher = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
            if (isTeacher) {
                return ResponseEntity.ok(classroomService.getClassroomsByTeacher(authentication.getName()));
            }
        }
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDTO> getClassroomById(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getClassroomById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDUCATION_OFFICER')")
    public ResponseEntity<ClassroomDTO> createClassroom(@Valid @RequestBody ClassroomDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomService.createClassroom(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDUCATION_OFFICER')")
    public ResponseEntity<ClassroomDTO> updateClassroom(@PathVariable Long id, @Valid @RequestBody ClassroomDTO dto) {
        return ResponseEntity.ok(classroomService.updateClassroom(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.noContent().build();
    }
}
