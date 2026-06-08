package com.school.student_management.controller;

import com.school.student_management.dto.UserAccountDTO;
import com.school.student_management.entity.UserAccount;
import com.school.student_management.repository.UserAccountRepository;
import com.school.student_management.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final UserAccountRepository userAccountRepository;
    private final com.school.student_management.repository.ClassroomRepository classroomRepository;
    private final com.school.student_management.repository.SubjectRepository subjectRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserAccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(userAccountService.getAllAccounts());
    }

    @GetMapping("/me")
    public ResponseEntity<UserAccountDTO> getMyProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        UserAccount acc = userAccountRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
        UserAccountDTO dto = new UserAccountDTO();
        dto.setId(acc.getId());
        dto.setUsername(acc.getUsername());
        dto.setRole(acc.getRole().name());
        dto.setEnabled(acc.isEnabled());
        if (acc.getStaff() != null) {
            dto.setStaffId(acc.getStaff().getId());
            dto.setStaffName(acc.getStaff().getFullName());
            
            // Populating homeroom class names
            List<com.school.student_management.entity.Classroom> classrooms = 
                classroomRepository.findByHomeroomTeacherId(acc.getStaff().getId());
            dto.setHomeroomClassNames(classrooms.stream()
                .map(com.school.student_management.entity.Classroom::getName)
                .collect(java.util.stream.Collectors.toList()));
                
            // Populating teaching subject names
            List<com.school.student_management.entity.Subject> subjects = 
                subjectRepository.findByTeacherId(acc.getStaff().getId());
            dto.setTeachingSubjectNames(subjects.stream()
                .map(com.school.student_management.entity.Subject::getName)
                .collect(java.util.stream.Collectors.toList()));
        }
        if (acc.getParent() != null) {
            dto.setParentId(acc.getParent().getId());
            dto.setParentName(acc.getParent().getFullName());
        }
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        userAccountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
