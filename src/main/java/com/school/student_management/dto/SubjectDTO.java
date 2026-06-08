package com.school.student_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubjectDTO {
    private Long id;

    @NotBlank(message = "Subject name is required")
    private String name;

    private String description;
    private Long teacherId;
    private String teacherName;
}
