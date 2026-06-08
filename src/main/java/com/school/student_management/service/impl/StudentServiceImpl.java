package com.school.student_management.service.impl;

import com.school.student_management.dto.StudentDTO;
import com.school.student_management.entity.Classroom;
import com.school.student_management.entity.Student;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.ClassroomRepository;
import com.school.student_management.repository.StudentRepository;
import com.school.student_management.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentDTO> getAllStudentsPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return toDTO(student);
    }

    @Override
    @Transactional
    public StudentDTO createStudent(StudentDTO dto) {
        Student student = toEntity(dto);
        return toDTO(studentRepository.save(student));
    }

    @Override
    @Transactional
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        student.setFullName(dto.getFullName());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(dto.getGender());
        student.setAddress(dto.getAddress());
        student.setPhone(dto.getPhone());
        student.setEmail(dto.getEmail());

        if (dto.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Classroom not found"));
            student.setClassroom(classroom);
        }

        return toDTO(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    public List<StudentDTO> getStudentsByClassroom(Long classroomId) {
        return studentRepository.findByClassroomId(classroomId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> searchStudents(String name) {
        return studentRepository.findByFullNameContainingIgnoreCase(name)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private StudentDTO toDTO(Student s) {
        StudentDTO dto = new StudentDTO();
        dto.setId(s.getId());
        dto.setFullName(s.getFullName());
        dto.setDateOfBirth(s.getDateOfBirth());
        dto.setGender(s.getGender());
        dto.setAddress(s.getAddress());
        dto.setPhone(s.getPhone());
        dto.setEmail(s.getEmail());
        dto.setStudentCode(s.getStudentCode());
        if (s.getClassroom() != null) {
            dto.setClassroomId(s.getClassroom().getId());
            dto.setClassroomName(s.getClassroom().getName());
        }
        return dto;
    }

    private Student toEntity(StudentDTO dto) {
        Student student = new Student();
        student.setFullName(dto.getFullName());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(dto.getGender());
        student.setAddress(dto.getAddress());
        student.setPhone(dto.getPhone());
        student.setEmail(dto.getEmail());
        student.setStudentCode(dto.getStudentCode());
        if (dto.getClassroomId() != null) {
            classroomRepository.findById(dto.getClassroomId())
                    .ifPresent(student::setClassroom);
        }
        return student;
    }

    @Override
    public List<StudentDTO> getStudentsByParent(String username) {
        return studentRepository.findByParentUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentDTO> getStudentsByParentPaginated(String username, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findByParentUsername(username, pageable).map(this::toDTO);
    }

    @Override
    public List<StudentDTO> getStudentsByTeacher(String username) {
        return studentRepository.findByTeacherUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentDTO> getStudentsByTeacherPaginated(String username, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findByTeacherUsername(username, pageable).map(this::toDTO);
    }
}