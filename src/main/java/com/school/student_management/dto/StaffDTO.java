package com.school.student_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StaffDTO {
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private LocalDate dateOfBirth;
    private String phone;
    private String email;
    private String role; // TEACHER, ADMIN, EDUCATION_OFFICER
    private Long schoolId;
    private String schoolName;
    
    // Optional field for account creation
    private String username;
    private String password;
}
