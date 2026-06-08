package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "grade_slips")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GradeSlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false, length = 100)
    private String title; // "Kiểm tra 15 phút lần 1", etc.

    @Column(name = "score_type", nullable = false, length = 20)
    private String scoreType; // "15MIN", "45MIN", "FINAL", "ORAL"

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal score;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(length = 250)
    private String remarks;
}
