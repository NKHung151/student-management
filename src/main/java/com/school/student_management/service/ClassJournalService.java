package com.school.student_management.service;

import com.school.student_management.dto.ClassJournalDTO;
import java.util.List;

public interface ClassJournalService {
    List<ClassJournalDTO> getAllJournals();
    ClassJournalDTO getJournalById(Long id);
    ClassJournalDTO createJournal(ClassJournalDTO dto);
    ClassJournalDTO updateJournal(Long id, ClassJournalDTO dto);
    void deleteJournal(Long id);
    List<ClassJournalDTO> getJournalsByClassroom(Long classroomId);
    List<ClassJournalDTO> getJournalsByTeacher(String username);
}
