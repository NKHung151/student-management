package com.school.student_management.dto;

import lombok.Data;

@Data
public class UserAccountDTO {
    private Long id;
    private String username;
    private String role; // ADMIN, TEACHER, EDUCATION_OFFICER, PARENT
    private boolean enabled;
    private Long staffId;
    private String staffName;
    private Long parentId;
    private String parentName;
    private java.util.List<String> homeroomClassNames;
    private java.util.List<String> teachingSubjectNames;
}
