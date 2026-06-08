package com.school.student_management.service.impl;

import com.school.student_management.dto.UserAccountDTO;
import com.school.student_management.entity.Parent;
import com.school.student_management.entity.Staff;
import com.school.student_management.entity.UserAccount;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.ParentRepository;
import com.school.student_management.repository.StaffRepository;
import com.school.student_management.repository.UserAccountRepository;
import com.school.student_management.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final StaffRepository staffRepository;
    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserAccountDTO> getAllAccounts() {
        return userAccountRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserAccountDTO getAccountById(Long id) {
        UserAccount acc = userAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return toDTO(acc);
    }

    @Override
    @Transactional
    public UserAccountDTO createAccount(UserAccountDTO dto) {
        if (userAccountRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        UserAccount acc = new UserAccount();
        acc.setUsername(dto.getUsername());
        acc.setPassword(passwordEncoder.encode("123456")); // default password
        acc.setRole(UserAccount.UserRole.valueOf(dto.getRole()));
        acc.setEnabled(dto.isEnabled());

        if (dto.getStaffId() != null) {
            Staff staff = staffRepository.findById(dto.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
            acc.setStaff(staff);
        }
        
        if (dto.getParentId() != null) {
            Parent parent = parentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
            acc.setParent(parent);
        }

        return toDTO(userAccountRepository.save(acc));
    }

    @Override
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        UserAccount acc = userAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        if (!passwordEncoder.matches(oldPassword, acc.getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }
        acc.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(acc);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        if (!userAccountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }
        userAccountRepository.deleteById(id);
    }

    private UserAccountDTO toDTO(UserAccount acc) {
        UserAccountDTO dto = new UserAccountDTO();
        dto.setId(acc.getId());
        dto.setUsername(acc.getUsername());
        dto.setRole(acc.getRole().name());
        dto.setEnabled(acc.isEnabled());
        if (acc.getStaff() != null) {
            dto.setStaffId(acc.getStaff().getId());
            dto.setStaffName(acc.getStaff().getFullName());
        }
        if (acc.getParent() != null) {
            dto.setParentId(acc.getParent().getId());
            dto.setParentName(acc.getParent().getFullName());
        }
        return dto;
    }
}
