package com.school.student_management.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ScoreRecordDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentCode;
    private Long subjectId;
    private String subjectName;
    private String semester;
    private String schoolYear;
    private BigDecimal score15min;
    private BigDecimal score45min;
    private BigDecimal scoreFinal;
    private BigDecimal averageScore;
}
