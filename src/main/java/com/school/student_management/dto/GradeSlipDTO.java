package com.school.student_management.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GradeSlipDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private String title;
    private String scoreType; // 15MIN, 45MIN, FINAL, ORAL
    private BigDecimal score;
    private LocalDate examDate;
    private String remarks;
}
