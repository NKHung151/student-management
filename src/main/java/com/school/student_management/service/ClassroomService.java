package com.school.student_management.service;

import com.school.student_management.dto.ClassroomDTO;
import java.util.List;

public interface ClassroomService {
    List<ClassroomDTO> getAllClassrooms();
    ClassroomDTO getClassroomById(Long id);
    ClassroomDTO createClassroom(ClassroomDTO dto);
    ClassroomDTO updateClassroom(Long id, ClassroomDTO dto);
    void deleteClassroom(Long id);
    List<ClassroomDTO> getClassroomsByTeacher(String username);
}
