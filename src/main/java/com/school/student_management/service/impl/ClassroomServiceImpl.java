package com.school.student_management.service.impl;

import com.school.student_management.dto.ClassroomDTO;
import com.school.student_management.entity.Classroom;
import com.school.student_management.entity.School;
import com.school.student_management.entity.Staff;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.ClassroomRepository;
import com.school.student_management.repository.SchoolRepository;
import com.school.student_management.repository.StaffRepository;
import com.school.student_management.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final SchoolRepository schoolRepository;
    private final StaffRepository staffRepository;

    @Override
    public List<ClassroomDTO> getAllClassrooms() {
        return classroomRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ClassroomDTO getClassroomById(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + id));
        return toDTO(classroom);
    }

    @Override
    @Transactional
    public ClassroomDTO createClassroom(ClassroomDTO dto) {
        Classroom classroom = toEntity(dto);
        return toDTO(classroomRepository.save(classroom));
    }

    @Override
    @Transactional
    public ClassroomDTO updateClassroom(Long id, ClassroomDTO dto) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + id));
        classroom.setName(dto.getName());
        classroom.setGradeLevel(dto.getGradeLevel());
        classroom.setSchoolYear(dto.getSchoolYear());
        
        if (dto.getSchoolId() != null) {
            School school = schoolRepository.findById(dto.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found"));
            classroom.setSchool(school);
        } else {
            classroom.setSchool(null);
        }

        if (dto.getHomeroomTeacherId() != null) {
            Staff teacher = staffRepository.findById(dto.getHomeroomTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
            classroom.setHomeroomTeacher(teacher);
        } else {
            classroom.setHomeroomTeacher(null);
        }

        return toDTO(classroomRepository.save(classroom));
    }

    @Override
    @Transactional
    public void deleteClassroom(Long id) {
        if (!classroomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Classroom not found with id: " + id);
        }
        classroomRepository.deleteById(id);
    }

    @Override
    public List<ClassroomDTO> getClassroomsByTeacher(String username) {
        return classroomRepository.findByTeacherUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ClassroomDTO toDTO(Classroom c) {
        ClassroomDTO dto = new ClassroomDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setGradeLevel(c.getGradeLevel());
        dto.setSchoolYear(c.getSchoolYear());
        if (c.getSchool() != null) {
            dto.setSchoolId(c.getSchool().getId());
            dto.setSchoolName(c.getSchool().getName());
        }
        if (c.getHomeroomTeacher() != null) {
            dto.setHomeroomTeacherId(c.getHomeroomTeacher().getId());
            dto.setHomeroomTeacherName(c.getHomeroomTeacher().getFullName());
        }
        return dto;
    }

    private Classroom toEntity(ClassroomDTO dto) {
        Classroom c = new Classroom();
        c.setName(dto.getName());
        c.setGradeLevel(dto.getGradeLevel());
        c.setSchoolYear(dto.getSchoolYear());
        if (dto.getSchoolId() != null) {
            schoolRepository.findById(dto.getSchoolId()).ifPresent(c::setSchool);
        }
        if (dto.getHomeroomTeacherId() != null) {
            staffRepository.findById(dto.getHomeroomTeacherId()).ifPresent(c::setHomeroomTeacher);
        }
        return c;
    }
}
