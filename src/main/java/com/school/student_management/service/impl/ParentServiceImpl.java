package com.school.student_management.service.impl;

import com.school.student_management.dto.ParentDTO;
import com.school.student_management.entity.Parent;
import com.school.student_management.entity.Student;
import com.school.student_management.entity.UserAccount;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.ParentRepository;
import com.school.student_management.repository.StudentRepository;
import com.school.student_management.repository.UserAccountRepository;
import com.school.student_management.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentServiceImpl implements ParentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<ParentDTO> getAllParents() {
        return parentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ParentDTO getParentById(Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + id));
        return toDTO(parent);
    }

    @Override
    @Transactional
    public ParentDTO createParent(ParentDTO dto) {
        Parent parent = toEntity(dto);
        parent = parentRepository.save(parent);

        // Update the back-relationship (many-to-many owner side)
        if (parent.getStudents() != null && !parent.getStudents().isEmpty()) {
            for (Student s : parent.getStudents()) {
                if (s.getParents() == null) {
                    s.setParents(new java.util.ArrayList<>());
                }
                if (!s.getParents().contains(parent)) {
                    s.getParents().add(parent);
                    studentRepository.save(s);
                }
            }
        }

        // If username is provided, create UserAccount
        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
            if (userAccountRepository.existsByUsername(dto.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            UserAccount account = UserAccount.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "123456"))
                    .role(UserAccount.UserRole.PARENT)
                    .enabled(true)
                    .parent(parent)
                    .build();
            userAccountRepository.save(account);
        }

        return toDTO(parent);
    }

    @Override
    @Transactional
    public ParentDTO updateParent(Long id, ParentDTO dto) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + id));
        
        parent.setFullName(dto.getFullName());
        parent.setPhone(dto.getPhone());
        parent.setEmail(dto.getEmail());
        parent.setOccupation(dto.getOccupation());
        parent.setAddress(dto.getAddress());

        if (dto.getStudentIds() != null) {
            List<Student> students = studentRepository.findAllById(dto.getStudentIds());
            parent.setStudents(students);
            
            // Update the back-relationship (many-to-many)
            for (Student s : students) {
                if (s.getParents() == null) {
                    s.setParents(new ArrayList<>());
                }
                if (!s.getParents().contains(parent)) {
                    s.getParents().add(parent);
                    studentRepository.save(s);
                }
            }
        }

        return toDTO(parentRepository.save(parent));
    }

    @Override
    @Transactional
    public void deleteParent(Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + id));
        
        // Remove relationships
        if (parent.getStudents() != null) {
            for (Student student : parent.getStudents()) {
                student.getParents().remove(parent);
                studentRepository.save(student);
            }
        }

        // Delete user account
        userAccountRepository.findAll().stream()
                .filter(acc -> acc.getParent() != null && acc.getParent().getId().equals(id))
                .findFirst()
                .ifPresent(userAccountRepository::delete);

        parentRepository.delete(parent);
    }

    private ParentDTO toDTO(Parent p) {
        ParentDTO dto = new ParentDTO();
        dto.setId(p.getId());
        dto.setFullName(p.getFullName());
        dto.setPhone(p.getPhone());
        dto.setEmail(p.getEmail());
        dto.setOccupation(p.getOccupation());
        dto.setAddress(p.getAddress());
        if (p.getStudents() != null) {
            dto.setStudentIds(p.getStudents().stream().map(Student::getId).collect(Collectors.toList()));
            dto.setStudentNames(p.getStudents().stream().map(Student::getFullName).collect(Collectors.toList()));
        } else {
            dto.setStudentIds(new ArrayList<>());
            dto.setStudentNames(new ArrayList<>());
        }
        
        // Find if there is a linked user account
        userAccountRepository.findAll().stream()
                .filter(acc -> acc.getParent() != null && acc.getParent().getId().equals(p.getId()))
                .findFirst()
                .ifPresent(acc -> dto.setUsername(acc.getUsername()));

        return dto;
    }

    private Parent toEntity(ParentDTO dto) {
        Parent p = new Parent();
        p.setFullName(dto.getFullName());
        p.setPhone(dto.getPhone());
        p.setEmail(dto.getEmail());
        p.setOccupation(dto.getOccupation());
        p.setAddress(dto.getAddress());
        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            p.setStudents(studentRepository.findAllById(dto.getStudentIds()));
        } else {
            p.setStudents(new ArrayList<>());
        }
        return p;
    }
}
