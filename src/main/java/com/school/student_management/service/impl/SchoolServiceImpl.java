package com.school.student_management.service.impl;

import com.school.student_management.dto.SchoolDTO;
import com.school.student_management.entity.School;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.SchoolRepository;
import com.school.student_management.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;

    @Override
    public List<SchoolDTO> getAllSchools() {
        return schoolRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public SchoolDTO getSchoolById(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));
        return toDTO(school);
    }

    @Override
    @Transactional
    public SchoolDTO createSchool(SchoolDTO dto) {
        School school = toEntity(dto);
        return toDTO(schoolRepository.save(school));
    }

    @Override
    @Transactional
    public SchoolDTO updateSchool(Long id, SchoolDTO dto) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));
        school.setName(dto.getName());
        school.setAddress(dto.getAddress());
        school.setPhone(dto.getPhone());
        school.setEmail(dto.getEmail());
        school.setSchoolType(dto.getSchoolType());
        return toDTO(schoolRepository.save(school));
    }

    @Override
    @Transactional
    public void deleteSchool(Long id) {
        if (!schoolRepository.existsById(id)) {
            throw new ResourceNotFoundException("School not found with id: " + id);
        }
        schoolRepository.deleteById(id);
    }

    private SchoolDTO toDTO(School s) {
        SchoolDTO dto = new SchoolDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setAddress(s.getAddress());
        dto.setPhone(s.getPhone());
        dto.setEmail(s.getEmail());
        dto.setSchoolType(s.getSchoolType());
        return dto;
    }

    private School toEntity(SchoolDTO dto) {
        School s = new School();
        s.setName(dto.getName());
        s.setAddress(dto.getAddress());
        s.setPhone(dto.getPhone());
        s.setEmail(dto.getEmail());
        s.setSchoolType(dto.getSchoolType());
        return s;
    }
}
