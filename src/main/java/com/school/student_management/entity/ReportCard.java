package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "report_cards")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "school_year", nullable = false, length = 20)
    private String schoolYear; // "2024-2025"

    @Column(name = "grade_level")
    private Integer gradeLevel; // 10, 11, 12

    @Column(precision = 4, scale = 2)
    private BigDecimal gpa;

    @Column(length = 20)
    private String conduct; // EXCELLENT (Tốt), GOOD (Khá), AVERAGE (Trung bình), WEAK (Yếu)

    @Column(name = "teacher_remarks", length = 1000)
    private String teacherRemarks;

    @Column(name = "parent_remarks", length = 1000)
    private String parentRemarks;

    private Boolean promoted; // Được lên lớp hay không
}
