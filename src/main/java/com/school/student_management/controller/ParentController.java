package com.school.student_management.controller;

import com.school.student_management.dto.ParentDTO;
import com.school.student_management.service.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @GetMapping
    public ResponseEntity<List<ParentDTO>> getAllParents() {
        return ResponseEntity.ok(parentService.getAllParents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParentDTO> getParentById(@PathVariable Long id) {
        return ResponseEntity.ok(parentService.getParentById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDUCATION_OFFICER')")
    public ResponseEntity<ParentDTO> createParent(@Valid @RequestBody ParentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parentService.createParent(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDUCATION_OFFICER')")
    public ResponseEntity<ParentDTO> updateParent(@PathVariable Long id, @Valid @RequestBody ParentDTO dto) {
        return ResponseEntity.ok(parentService.updateParent(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }
}
