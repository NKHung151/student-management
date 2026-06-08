package com.school.student_management.service;

import com.school.student_management.dto.StudentCardDTO;
import java.util.List;

public interface StudentCardService {
    List<StudentCardDTO> getAllCards();
    StudentCardDTO getCardById(Long id);
    StudentCardDTO createCard(StudentCardDTO dto);
    StudentCardDTO updateCard(Long id, StudentCardDTO dto);
    void deleteCard(Long id);
    StudentCardDTO getCardByStudent(Long studentId);
    List<StudentCardDTO> getCardsByParent(String username);
}
