package com.school.student_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class ParentDTO {
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;
    private String email;
    private String occupation;
    private String address;
    private List<Long> studentIds;
    private List<String> studentNames;
    
    // Optional field for account creation
    private String username;
    private String password;
}
