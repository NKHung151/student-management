package com.school.student_management.service;

import com.school.student_management.dto.ReportCardDTO;
import java.util.List;

public interface ReportCardService {
    List<ReportCardDTO> getAllReportCards();
    ReportCardDTO getReportCardById(Long id);
    ReportCardDTO createReportCard(ReportCardDTO dto);
    ReportCardDTO updateReportCard(Long id, ReportCardDTO dto);
    void deleteReportCard(Long id);
    List<ReportCardDTO> getReportCardsByStudent(Long studentId);
    List<ReportCardDTO> getReportCardsByParent(String username);
    List<ReportCardDTO> getReportCardsByTeacher(String username);
}
