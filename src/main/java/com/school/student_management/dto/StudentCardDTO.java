package com.school.student_management.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentCardDTO {
    private Long id;
    private String cardNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status; // ACTIVE, EXPIRED, LOCKED
    private Long studentId;
    private String studentName;
    private String studentCode;
}
