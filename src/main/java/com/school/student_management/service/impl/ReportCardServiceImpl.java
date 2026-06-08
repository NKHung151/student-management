package com.school.student_management.service.impl;

import com.school.student_management.dto.ReportCardDTO;
import com.school.student_management.entity.ReportCard;
import com.school.student_management.entity.Student;
import com.school.student_management.entity.UserAccount;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.ReportCardRepository;
import com.school.student_management.repository.StudentRepository;
import com.school.student_management.repository.UserAccountRepository;
import com.school.student_management.service.ReportCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportCardServiceImpl implements ReportCardService {

    private final ReportCardRepository reportCardRepository;
    private final StudentRepository studentRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public List<ReportCardDTO> getAllReportCards() {
        return reportCardRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ReportCardDTO getReportCardById(Long id) {
        ReportCard rc = reportCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report card not found with id: " + id));
        return toDTO(rc);
    }

    private void validateReportCardWriteAccess(Student student, ReportCardDTO dto, ReportCard existingRC) {
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

        boolean isParent = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARENT"));
        if (isParent) {
            if (student == null) {
                throw new org.springframework.security.access.AccessDeniedException("Student not found");
            }
            UserAccount acc = userAccountRepository.findByUsername(auth.getName()).orElse(null);
            if (acc == null || acc.getParent() == null) {
                throw new org.springframework.security.access.AccessDeniedException("Parent profile not found");
            }
            Long parentId = acc.getParent().getId();
            boolean isOwnChild = student.getParents().stream()
                    .anyMatch(p -> p.getId().equals(parentId));
            if (!isOwnChild) {
                throw new org.springframework.security.access.AccessDeniedException("You can only manage report cards of your own children");
            }
            if (existingRC != null && dto != null) {
                if ((dto.getGpa() != null && (existingRC.getGpa() == null || dto.getGpa().compareTo(existingRC.getGpa()) != 0)) ||
                    (dto.getConduct() != null && !dto.getConduct().equals(existingRC.getConduct())) ||
                    (dto.getTeacherRemarks() != null && !dto.getTeacherRemarks().equals(existingRC.getTeacherRemarks())) ||
                    (dto.getPromoted() != null && !dto.getPromoted().equals(existingRC.getPromoted()))) {
                    throw new org.springframework.security.access.AccessDeniedException("Parents can only update parent remarks");
                }
            }
            return;
        }

        boolean isTeacher = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        if (isTeacher) {
            if (student == null) {
                throw new org.springframework.security.access.AccessDeniedException("Student not found");
            }
            UserAccount acc = userAccountRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("User account not found"));
            if (acc.getStaff() == null) {
                throw new org.springframework.security.access.AccessDeniedException("Staff profile not found");
            }
            if (student.getClassroom() == null || student.getClassroom().getHomeroomTeacher() == null ||
                !student.getClassroom().getHomeroomTeacher().getId().equals(acc.getStaff().getId())) {
                throw new org.springframework.security.access.AccessDeniedException("Only the homeroom teacher of this classroom can edit student report cards");
            }
        } else {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
    }

    @Override
    @Transactional
    public ReportCardDTO createReportCard(ReportCardDTO dto) {
        Student student = null;
        if (dto.getStudentId() != null) {
            student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        }
        validateReportCardWriteAccess(student, dto, null);
        ReportCard rc = toEntity(dto);
        return toDTO(reportCardRepository.save(rc));
    }

    @Override
    @Transactional
    public ReportCardDTO updateReportCard(Long id, ReportCardDTO dto) {
        ReportCard rc = reportCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report card not found with id: " + id));
        validateReportCardWriteAccess(rc.getStudent(), dto, rc);
        
        rc.setSchoolYear(dto.getSchoolYear());
        rc.setGradeLevel(dto.getGradeLevel());
        rc.setGpa(dto.getGpa());
        rc.setConduct(dto.getConduct());
        rc.setTeacherRemarks(dto.getTeacherRemarks());
        rc.setParentRemarks(dto.getParentRemarks());
        rc.setPromoted(dto.getPromoted());

        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            rc.setStudent(student);
        }

        return toDTO(reportCardRepository.save(rc));
    }

    @Override
    @Transactional
    public void deleteReportCard(Long id) {
        ReportCard rc = reportCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report card not found with id: " + id));
        validateReportCardWriteAccess(rc.getStudent(), null, rc);
        reportCardRepository.delete(rc);
    }

    @Override
    public List<ReportCardDTO> getReportCardsByStudent(Long studentId) {
        return reportCardRepository.findByStudentId(studentId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ReportCardDTO toDTO(ReportCard rc) {
        ReportCardDTO dto = new ReportCardDTO();
        dto.setId(rc.getId());
        dto.setSchoolYear(rc.getSchoolYear());
        dto.setGradeLevel(rc.getGradeLevel());
        dto.setGpa(rc.getGpa());
        dto.setConduct(rc.getConduct());
        dto.setTeacherRemarks(rc.getTeacherRemarks());
        dto.setParentRemarks(rc.getParentRemarks());
        dto.setPromoted(rc.getPromoted());
        if (rc.getStudent() != null) {
            dto.setStudentId(rc.getStudent().getId());
            dto.setStudentName(rc.getStudent().getFullName());
            dto.setStudentCode(rc.getStudent().getStudentCode());
        }
        return dto;
    }

    private ReportCard toEntity(ReportCardDTO dto) {
        ReportCard rc = new ReportCard();
        rc.setSchoolYear(dto.getSchoolYear());
        rc.setGradeLevel(dto.getGradeLevel());
        rc.setGpa(dto.getGpa());
        rc.setConduct(dto.getConduct());
        rc.setTeacherRemarks(dto.getTeacherRemarks());
        rc.setParentRemarks(dto.getParentRemarks());
        rc.setPromoted(dto.getPromoted());
        
        if (dto.getStudentId() != null) {
            studentRepository.findById(dto.getStudentId()).ifPresent(rc::setStudent);
        }
        return rc;
    }

    @Override
    public List<ReportCardDTO> getReportCardsByParent(String username) {
        return reportCardRepository.findByParentUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportCardDTO> getReportCardsByTeacher(String username) {
        return reportCardRepository.findByTeacherUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
