package com.school.student_management.service;

import com.school.student_management.dto.SubjectDTO;
import java.util.List;

public interface SubjectService {
    List<SubjectDTO> getAllSubjects();
    SubjectDTO getSubjectById(Long id);
    SubjectDTO createSubject(SubjectDTO dto);
    SubjectDTO updateSubject(Long id, SubjectDTO dto);
    void deleteSubject(Long id);
    List<SubjectDTO> getSubjectsByTeacher(Long teacherId);
}
