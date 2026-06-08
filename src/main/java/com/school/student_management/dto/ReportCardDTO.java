package com.school.student_management.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReportCardDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentCode;
    private String schoolYear;
    private Integer gradeLevel;
    private BigDecimal gpa;
    private String conduct;
    private String teacherRemarks;
    private String parentRemarks;
    private Boolean promoted;
}
