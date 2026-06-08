package com.school.student_management.service.impl;

import com.school.student_management.dto.SubjectDTO;
import com.school.student_management.entity.Staff;
import com.school.student_management.entity.Subject;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.StaffRepository;
import com.school.student_management.repository.SubjectRepository;
import com.school.student_management.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final StaffRepository staffRepository;

    @Override
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public SubjectDTO getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
        return toDTO(subject);
    }

    @Override
    @Transactional
    public SubjectDTO createSubject(SubjectDTO dto) {
        Subject subject = toEntity(dto);
        return toDTO(subjectRepository.save(subject));
    }

    @Override
    @Transactional
    public SubjectDTO updateSubject(Long id, SubjectDTO dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
        subject.setName(dto.getName());
        subject.setDescription(dto.getDescription());
        
        if (dto.getTeacherId() != null) {
            Staff teacher = staffRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
            subject.setTeacher(teacher);
        } else {
            subject.setTeacher(null);
        }

        return toDTO(subjectRepository.save(subject));
    }

    @Override
    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }

    @Override
    public List<SubjectDTO> getSubjectsByTeacher(Long teacherId) {
        return subjectRepository.findByTeacherId(teacherId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private SubjectDTO toDTO(Subject s) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        if (s.getTeacher() != null) {
            dto.setTeacherId(s.getTeacher().getId());
            dto.setTeacherName(s.getTeacher().getFullName());
        }
        return dto;
    }

    private Subject toEntity(SubjectDTO dto) {
        Subject s = new Subject();
        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        if (dto.getTeacherId() != null) {
            staffRepository.findById(dto.getTeacherId()).ifPresent(s::setTeacher);
        }
        return s;
    }
}
