package com.school.student_management.service;

import com.school.student_management.dto.GradeSlipDTO;
import java.util.List;

public interface GradeSlipService {
    List<GradeSlipDTO> getAllGradeSlips();
    GradeSlipDTO getGradeSlipById(Long id);
    GradeSlipDTO createGradeSlip(GradeSlipDTO dto);
    GradeSlipDTO updateGradeSlip(Long id, GradeSlipDTO dto);
    void deleteGradeSlip(Long id);
    List<GradeSlipDTO> getGradeSlipsByStudent(Long studentId);
    List<GradeSlipDTO> getGradeSlipsByParent(String username);
    List<GradeSlipDTO> getGradeSlipsByTeacher(String username);
}
