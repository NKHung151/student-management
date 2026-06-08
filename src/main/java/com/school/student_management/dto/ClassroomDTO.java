package com.school.student_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClassroomDTO {
    private Long id;

    @NotBlank(message = "Classroom name is required")
    private String name;

    private Integer gradeLevel;
    private String schoolYear;
    private Long schoolId;
    private String schoolName;
    private Long homeroomTeacherId;
    private String homeroomTeacherName;
}
