package com.school.student_management.service.impl;

import com.school.student_management.dto.ScoreRecordDTO;
import com.school.student_management.entity.ScoreRecord;
import com.school.student_management.entity.Student;
import com.school.student_management.entity.Subject;
import com.school.student_management.entity.UserAccount;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.ScoreRecordRepository;
import com.school.student_management.repository.StudentRepository;
import com.school.student_management.repository.SubjectRepository;
import com.school.student_management.repository.UserAccountRepository;
import com.school.student_management.service.ScoreRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreRecordServiceImpl implements ScoreRecordService {

    private final ScoreRecordRepository scoreRecordRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public List<ScoreRecordDTO> getAllScores() {
        return scoreRecordRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ScoreRecordDTO getScoreById(Long id) {
        ScoreRecord scoreRecord = scoreRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Score record not found with id: " + id));
        return toDTO(scoreRecord);
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
            throw new org.springframework.security.access.AccessDeniedException("Only teachers, education officers, or admins can manage scores");
        }
    }

    @Override
    @Transactional
    public ScoreRecordDTO createScore(ScoreRecordDTO dto) {
        if (dto.getSubjectId() != null) {
            validateTeacherSubjectAssignment(dto.getSubjectId());
        }
        ScoreRecord scoreRecord = toEntity(dto);
        calculateAverage(scoreRecord);
        return toDTO(scoreRecordRepository.save(scoreRecord));
    }

    @Override
    @Transactional
    public ScoreRecordDTO updateScore(Long id, ScoreRecordDTO dto) {
        ScoreRecord scoreRecord = scoreRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Score record not found with id: " + id));
        if (scoreRecord.getSubject() != null) {
            validateTeacherSubjectAssignment(scoreRecord.getSubject().getId());
        }
        
        scoreRecord.setSemester(dto.getSemester());
        scoreRecord.setSchoolYear(dto.getSchoolYear());
        scoreRecord.setScore15min(dto.getScore15min());
        scoreRecord.setScore45min(dto.getScore45min());
        scoreRecord.setScoreFinal(dto.getScoreFinal());
        
        calculateAverage(scoreRecord);
        
        return toDTO(scoreRecordRepository.save(scoreRecord));
    }

    @Override
    @Transactional
    public void deleteScore(Long id) {
        ScoreRecord scoreRecord = scoreRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Score record not found with id: " + id));
        if (scoreRecord.getSubject() != null) {
            validateTeacherSubjectAssignment(scoreRecord.getSubject().getId());
        }
        scoreRecordRepository.delete(scoreRecord);
    }

    @Override
    public List<ScoreRecordDTO> getScoresByStudent(Long studentId) {
        return scoreRecordRepository.findByStudentId(studentId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private void calculateAverage(ScoreRecord record) {
        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;
        
        if (record.getScore15min() != null) {
            sum = sum.add(record.getScore15min());
            count += 1;
        }
        if (record.getScore45min() != null) {
            sum = sum.add(record.getScore45min().multiply(BigDecimal.valueOf(2)));
            count += 2;
        }
        if (record.getScoreFinal() != null) {
            sum = sum.add(record.getScoreFinal().multiply(BigDecimal.valueOf(3)));
            count += 3;
        }
        
        if (count > 0) {
            BigDecimal avg = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            record.setAverageScore(avg);
        } else {
            record.setAverageScore(null);
        }
    }

    private ScoreRecordDTO toDTO(ScoreRecord s) {
        ScoreRecordDTO dto = new ScoreRecordDTO();
        dto.setId(s.getId());
        dto.setSemester(s.getSemester());
        dto.setSchoolYear(s.getSchoolYear());
        dto.setScore15min(s.getScore15min());
        dto.setScore45min(s.getScore45min());
        dto.setScoreFinal(s.getScoreFinal());
        dto.setAverageScore(s.getAverageScore());
        if (s.getStudent() != null) {
            dto.setStudentId(s.getStudent().getId());
            dto.setStudentName(s.getStudent().getFullName());
            dto.setStudentCode(s.getStudent().getStudentCode());
        }
        if (s.getSubject() != null) {
            dto.setSubjectId(s.getSubject().getId());
            dto.setSubjectName(s.getSubject().getName());
        }
        return dto;
    }

    private ScoreRecord toEntity(ScoreRecordDTO dto) {
        ScoreRecord s = new ScoreRecord();
        s.setSemester(dto.getSemester());
        s.setSchoolYear(dto.getSchoolYear());
        s.setScore15min(dto.getScore15min());
        s.setScore45min(dto.getScore45min());
        s.setScoreFinal(dto.getScoreFinal());
        
        if (dto.getStudentId() != null) {
            studentRepository.findById(dto.getStudentId()).ifPresent(s::setStudent);
        }
        if (dto.getSubjectId() != null) {
            subjectRepository.findById(dto.getSubjectId()).ifPresent(s::setSubject);
        }
        return s;
    }

    @Override
    public List<ScoreRecordDTO> getScoresByParent(String username) {
        return scoreRecordRepository.findByParentUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScoreRecordDTO> getScoresByTeacher(String username) {
        return scoreRecordRepository.findByTeacherUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
