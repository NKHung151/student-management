package com.school.student_management.service.impl;

import com.school.student_management.dto.StaffDTO;
import com.school.student_management.entity.School;
import com.school.student_management.entity.Staff;
import com.school.student_management.entity.UserAccount;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.SchoolRepository;
import com.school.student_management.repository.StaffRepository;
import com.school.student_management.repository.UserAccountRepository;
import com.school.student_management.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final SchoolRepository schoolRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<StaffDTO> getAllStaffs() {
        return staffRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public StaffDTO getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + id));
        return toDTO(staff);
    }

    @Override
    @Transactional
    public StaffDTO createStaff(StaffDTO dto) {
        Staff staff = toEntity(dto);
        staff = staffRepository.save(staff);

        // If username is provided, automatically create UserAccount
        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
            if (userAccountRepository.existsByUsername(dto.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            UserAccount account = UserAccount.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "123456"))
                    .role(UserAccount.UserRole.valueOf(dto.getRole()))
                    .enabled(true)
                    .staff(staff)
                    .build();
            userAccountRepository.save(account);
        }

        return toDTO(staff);
    }

    @Override
    @Transactional
    public StaffDTO updateStaff(Long id, StaffDTO dto) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + id));
        
        staff.setFullName(dto.getFullName());
        staff.setDateOfBirth(dto.getDateOfBirth());
        staff.setPhone(dto.getPhone());
        staff.setEmail(dto.getEmail());
        staff.setRole(Staff.StaffRole.valueOf(dto.getRole()));

        if (dto.getSchoolId() != null) {
            School school = schoolRepository.findById(dto.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found"));
            staff.setSchool(school);
        } else {
            staff.setSchool(null);
        }

        return toDTO(staffRepository.save(staff));
    }

    @Override
    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + id));
        
        // Remove linked user account
        if (staff.getUserAccount() != null) {
            userAccountRepository.delete(staff.getUserAccount());
        }
        
        staffRepository.delete(staff);
    }

    private StaffDTO toDTO(Staff s) {
        StaffDTO dto = new StaffDTO();
        dto.setId(s.getId());
        dto.setFullName(s.getFullName());
        dto.setDateOfBirth(s.getDateOfBirth());
        dto.setPhone(s.getPhone());
        dto.setEmail(s.getEmail());
        dto.setRole(s.getRole().name());
        if (s.getSchool() != null) {
            dto.setSchoolId(s.getSchool().getId());
            dto.setSchoolName(s.getSchool().getName());
        }
        if (s.getUserAccount() != null) {
            dto.setUsername(s.getUserAccount().getUsername());
        }
        return dto;
    }

    private Staff toEntity(StaffDTO dto) {
        Staff s = new Staff();
        s.setFullName(dto.getFullName());
        s.setDateOfBirth(dto.getDateOfBirth());
        s.setPhone(dto.getPhone());
        s.setEmail(dto.getEmail());
        s.setRole(Staff.StaffRole.valueOf(dto.getRole()));
        if (dto.getSchoolId() != null) {
            schoolRepository.findById(dto.getSchoolId()).ifPresent(s::setSchool);
        }
        return s;
    }
}
