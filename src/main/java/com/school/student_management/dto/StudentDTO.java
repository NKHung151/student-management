package com.school.student_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentDTO {
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String phone;
    private String email;
    private String studentCode;
    private String avatarUrl;
    private Long classroomId;
    private String classroomName;
}