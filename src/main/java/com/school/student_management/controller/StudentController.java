package com.school.student_management.controller;

import com.school.student_management.dto.StudentDTO;
import com.school.student_management.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<?> getAllStudents(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        boolean isParent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARENT"));
        boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        
        if (isParent) {
            String username = authentication.getName();
            if (page != null && size != null) {
                return ResponseEntity.ok(studentService.getStudentsByParentPaginated(username, page, size, sortBy, sortDir));
            }
            return ResponseEntity.ok(studentService.getStudentsByParent(username));
        }
        
        if (isTeacher) {
            String username = authentication.getName();
            if (page != null && size != null) {
                return ResponseEntity.ok(studentService.getStudentsByTeacherPaginated(username, page, size, sortBy, sortDir));
            }
            return ResponseEntity.ok(studentService.getStudentsByTeacher(username));
        }
        
        if (page != null && size != null) {
            return ResponseEntity.ok(studentService.getAllStudentsPaginated(page, size, sortBy, sortDir));
        }
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudentDTO>> searchStudents(@RequestParam String name) {
        return ResponseEntity.ok(studentService.searchStudents(name));
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<StudentDTO>> getByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(studentService.getStudentsByClassroom(classroomId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDUCATION_OFFICER')")
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.createStudent(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDUCATION_OFFICER')")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentDTO dto) {
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}