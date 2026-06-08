package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "class_journals")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClassJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Staff teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "teaching_date", nullable = false)
    private LocalDate teachingDate;

    @Column(name = "lesson_period")
    private Integer lessonPeriod; // Tiết học (1, 2, 3, 4, 5)

    @Column(name = "lesson_content", length = 500)
    private String lessonContent;

    @Column(name = "student_attendance", length = 200)
    private String studentAttendance; // Danh sách học sinh vắng

    @Column(length = 500)
    private String remarks; // Nhận xét của giáo viên về tiết học

    private Integer score; // Điểm tiết học (1-10)
}
