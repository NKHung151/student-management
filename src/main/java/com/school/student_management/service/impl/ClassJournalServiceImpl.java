package com.school.student_management.service.impl;

import com.school.student_management.dto.ClassJournalDTO;
import com.school.student_management.entity.ClassJournal;
import com.school.student_management.entity.Classroom;
import com.school.student_management.entity.Staff;
import com.school.student_management.entity.Subject;
import com.school.student_management.entity.UserAccount;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.ClassJournalRepository;
import com.school.student_management.repository.ClassroomRepository;
import com.school.student_management.repository.StaffRepository;
import com.school.student_management.repository.SubjectRepository;
import com.school.student_management.repository.UserAccountRepository;
import com.school.student_management.service.ClassJournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassJournalServiceImpl implements ClassJournalService {

    private final ClassJournalRepository classJournalRepository;
    private final ClassroomRepository classroomRepository;
    private final StaffRepository staffRepository;
    private final SubjectRepository subjectRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public List<ClassJournalDTO> getAllJournals() {
        return classJournalRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ClassJournalDTO getJournalById(Long id) {
        ClassJournal journal = classJournalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class journal not found with id: " + id));
        return toDTO(journal);
    }

    private void validateTeacherJournalAssignment(Long journalTeacherId) {
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
            if (journalTeacherId == null || !journalTeacherId.equals(acc.getStaff().getId())) {
                throw new org.springframework.security.access.AccessDeniedException("You can only manage journals assigned to yourself");
            }
        } else {
            throw new org.springframework.security.access.AccessDeniedException("Only teachers, education officers, or admins can manage journals");
        }
    }

    @Override
    @Transactional
    public ClassJournalDTO createJournal(ClassJournalDTO dto) {
        if (dto.getTeacherId() != null) {
            validateTeacherJournalAssignment(dto.getTeacherId());
        }
        ClassJournal journal = toEntity(dto);
        return toDTO(classJournalRepository.save(journal));
    }

    @Override
    @Transactional
    public ClassJournalDTO updateJournal(Long id, ClassJournalDTO dto) {
        ClassJournal journal = classJournalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class journal not found with id: " + id));
        if (journal.getTeacher() != null) {
            validateTeacherJournalAssignment(journal.getTeacher().getId());
        }
        
        journal.setTeachingDate(dto.getTeachingDate());
        journal.setLessonPeriod(dto.getLessonPeriod());
        journal.setLessonContent(dto.getLessonContent());
        journal.setStudentAttendance(dto.getStudentAttendance());
        journal.setRemarks(dto.getRemarks());
        journal.setScore(dto.getScore());

        if (dto.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Classroom not found"));
            journal.setClassroom(classroom);
        }

        if (dto.getTeacherId() != null) {
            Staff teacher = staffRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
            journal.setTeacher(teacher);
        }

        if (dto.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
            journal.setSubject(subject);
        }

        return toDTO(classJournalRepository.save(journal));
    }

    @Override
    @Transactional
    public void deleteJournal(Long id) {
        ClassJournal journal = classJournalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class journal not found with id: " + id));
        if (journal.getTeacher() != null) {
            validateTeacherJournalAssignment(journal.getTeacher().getId());
        }
        classJournalRepository.delete(journal);
    }

    @Override
    public List<ClassJournalDTO> getJournalsByClassroom(Long classroomId) {
        return classJournalRepository.findByClassroomId(classroomId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ClassJournalDTO> getJournalsByTeacher(String username) {
        return classJournalRepository.findByTeacherUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ClassJournalDTO toDTO(ClassJournal j) {
        ClassJournalDTO dto = new ClassJournalDTO();
        dto.setId(j.getId());
        dto.setTeachingDate(j.getTeachingDate());
        dto.setLessonPeriod(j.getLessonPeriod());
        dto.setLessonContent(j.getLessonContent());
        dto.setStudentAttendance(j.getStudentAttendance());
        dto.setRemarks(j.getRemarks());
        dto.setScore(j.getScore());
        if (j.getClassroom() != null) {
            dto.setClassroomId(j.getClassroom().getId());
            dto.setClassroomName(j.getClassroom().getName());
        }
        if (j.getTeacher() != null) {
            dto.setTeacherId(j.getTeacher().getId());
            dto.setTeacherName(j.getTeacher().getFullName());
        }
        if (j.getSubject() != null) {
            dto.setSubjectId(j.getSubject().getId());
            dto.setSubjectName(j.getSubject().getName());
        }
        return dto;
    }

    private ClassJournal toEntity(ClassJournalDTO dto) {
        ClassJournal j = new ClassJournal();
        j.setTeachingDate(dto.getTeachingDate());
        j.setLessonPeriod(dto.getLessonPeriod());
        j.setLessonContent(dto.getLessonContent());
        j.setStudentAttendance(dto.getStudentAttendance());
        j.setRemarks(dto.getRemarks());
        j.setScore(dto.getScore());
        
        if (dto.getClassroomId() != null) {
            classroomRepository.findById(dto.getClassroomId()).ifPresent(j::setClassroom);
        }
        if (dto.getTeacherId() != null) {
            staffRepository.findById(dto.getTeacherId()).ifPresent(j::setTeacher);
        }
        if (dto.getSubjectId() != null) {
            subjectRepository.findById(dto.getSubjectId()).ifPresent(j::setSubject);
        }
        return j;
    }
}
