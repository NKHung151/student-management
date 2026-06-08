package com.school.student_management.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClassJournalDTO {
    private Long id;
    private Long classroomId;
    private String classroomName;
    private Long teacherId;
    private String teacherName;
    private Long subjectId;
    private String subjectName;
    private LocalDate teachingDate;
    private Integer lessonPeriod;
    private String lessonContent;
    private String studentAttendance;
    private String remarks;
    private Integer score;
}
