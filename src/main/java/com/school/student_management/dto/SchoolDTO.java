package com.school.student_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SchoolDTO {
    private Long id;

    @NotBlank(message = "School name is required")
    private String name;

    private String address;
    private String phone;
    private String email;
    private String schoolType;
}
