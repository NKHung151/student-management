package com.school.student_management.service.impl;

import com.school.student_management.dto.GradeSlipDTO;
import com.school.student_management.entity.GradeSlip;
import com.school.student_management.entity.Student;
import com.school.student_management.entity.Subject;
import com.school.student_management.entity.UserAccount;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.GradeSlipRepository;
import com.school.student_management.repository.StudentRepository;
import com.school.student_management.repository.SubjectRepository;
import com.school.student_management.repository.UserAccountRepository;
import com.school.student_management.service.GradeSlipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeSlipServiceImpl implements GradeSlipService {

    private final GradeSlipRepository gradeSlipRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public List<GradeSlipDTO> getAllGradeSlips() {
        return gradeSlipRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public GradeSlipDTO getGradeSlipById(Long id) {
        GradeSlip gradeSlip = gradeSlipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade slip not found with id: " + id));
        return toDTO(gradeSlip);
    }

    private void validateTeacherSubjectAssignment(Long subjectId) {
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }
        
        boolean isAdminOrOfficer = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_EDUCATION_OFFICER"));
        if (isAdminOrOfficer) {
            return; // bypass validation
        }

        boolean isTeacher = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        if (isTeacher) {
            UserAccount acc = userAccountRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("User account not found"));
            if (acc.getStaff() == null) {
                throw new org.springframework.security.access.AccessDeniedException("Staff profile not found");
            }
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
            if (subject.getTeacher() == null || !subject.getTeacher().getId().equals(acc.getStaff().getId())) {
                throw new org.springframework.security.access.AccessDeniedException("You are not assigned to teach this subject");
            }
        } else {
            throw new org.springframework.security.access.AccessDeniedException("Only teachers, education officers, or admins can manage grade slips");
        }
    }

    @Override
    @Transactional
    public GradeSlipDTO createGradeSlip(GradeSlipDTO dto) {
        if (dto.getSubjectId() != null) {
            validateTeacherSubjectAssignment(dto.getSubjectId());
        }
        GradeSlip gradeSlip = toEntity(dto);
        return toDTO(gradeSlipRepository.save(gradeSlip));
    }

    @Override
    @Transactional
    public GradeSlipDTO updateGradeSlip(Long id, GradeSlipDTO dto) {
        GradeSlip gradeSlip = gradeSlipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade slip not found with id: " + id));
        if (gradeSlip.getSubject() != null) {
            validateTeacherSubjectAssignment(gradeSlip.getSubject().getId());
        }
        
        gradeSlip.setTitle(dto.getTitle());
        gradeSlip.setScoreType(dto.getScoreType());
        gradeSlip.setScore(dto.getScore());
        gradeSlip.setExamDate(dto.getExamDate());
        gradeSlip.setRemarks(dto.getRemarks());

        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            gradeSlip.setStudent(student);
        }

        if (dto.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
            gradeSlip.setSubject(subject);
        }

        return toDTO(gradeSlipRepository.save(gradeSlip));
    }

    @Override
    @Transactional
    public void deleteGradeSlip(Long id) {
        GradeSlip gradeSlip = gradeSlipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade slip not found with id: " + id));
        if (gradeSlip.getSubject() != null) {
            validateTeacherSubjectAssignment(gradeSlip.getSubject().getId());
        }
        gradeSlipRepository.delete(gradeSlip);
    }

    @Override
    public List<GradeSlipDTO> getGradeSlipsByStudent(Long studentId) {
        return gradeSlipRepository.findByStudentId(studentId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<GradeSlipDTO> getGradeSlipsByParent(String username) {
        return gradeSlipRepository.findByParentUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeSlipDTO> getGradeSlipsByTeacher(String username) {
        return gradeSlipRepository.findByTeacherUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private GradeSlipDTO toDTO(GradeSlip g) {
        GradeSlipDTO dto = new GradeSlipDTO();
        dto.setId(g.getId());
        dto.setTitle(g.getTitle());
        dto.setScoreType(g.getScoreType());
        dto.setScore(g.getScore());
        dto.setExamDate(g.getExamDate());
        dto.setRemarks(g.getRemarks());
        if (g.getStudent() != null) {
            dto.setStudentId(g.getStudent().getId());
            dto.setStudentName(g.getStudent().getFullName());
        }
        if (g.getSubject() != null) {
            dto.setSubjectId(g.getSubject().getId());
            dto.setSubjectName(g.getSubject().getName());
        }
        return dto;
    }

    private GradeSlip toEntity(GradeSlipDTO dto) {
        GradeSlip g = new GradeSlip();
        g.setTitle(dto.getTitle());
        g.setScoreType(dto.getScoreType());
        g.setScore(dto.getScore());
        g.setExamDate(dto.getExamDate());
        g.setRemarks(dto.getRemarks());
        
        if (dto.getStudentId() != null) {
            studentRepository.findById(dto.getStudentId()).ifPresent(g::setStudent);
        }
        if (dto.getSubjectId() != null) {
            subjectRepository.findById(dto.getSubjectId()).ifPresent(g::setSubject);
        }
        return g;
    }
}
