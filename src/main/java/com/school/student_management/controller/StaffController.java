package com.school.student_management.controller;

import com.school.student_management.dto.StaffDTO;
import com.school.student_management.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<List<StaffDTO>> getAllStaffs() {
        return ResponseEntity.ok(staffService.getAllStaffs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffDTO> getStaffById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaffDTO> createStaff(@Valid @RequestBody StaffDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.createStaff(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaffDTO> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffDTO dto) {
        return ResponseEntity.ok(staffService.updateStaff(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}
