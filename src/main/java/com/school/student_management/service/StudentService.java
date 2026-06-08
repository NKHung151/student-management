package com.school.student_management.service;

import com.school.student_management.dto.StudentDTO;
import org.springframework.data.domain.Page;
import java.util.List;

public interface StudentService {
    List<StudentDTO> getAllStudents();
    Page<StudentDTO> getAllStudentsPaginated(int page, int size, String sortBy, String sortDir);
    StudentDTO getStudentById(Long id);
    StudentDTO createStudent(StudentDTO dto);
    StudentDTO updateStudent(Long id, StudentDTO dto);
    void deleteStudent(Long id);
    List<StudentDTO> getStudentsByClassroom(Long classroomId);
    List<StudentDTO> searchStudents(String name);
    List<StudentDTO> getStudentsByParent(String username);
    Page<StudentDTO> getStudentsByParentPaginated(String username, int page, int size, String sortBy, String sortDir);
    List<StudentDTO> getStudentsByTeacher(String username);
    Page<StudentDTO> getStudentsByTeacherPaginated(String username, int page, int size, String sortBy, String sortDir);
}