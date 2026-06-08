package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "score_records")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ScoreRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(length = 20)
    private String semester;         // "HK1", "HK2"

    @Column(name = "school_year", length = 20)
    private String schoolYear;

    @Column(name = "score_15min", precision = 4, scale = 2)
    private BigDecimal score15min;

    @Column(name = "score_45min", precision = 4, scale = 2)
    private BigDecimal score45min;

    @Column(name = "score_final", precision = 4, scale = 2)
    private BigDecimal scoreFinal;

    @Column(name = "average_score", precision = 4, scale = 2)
    private BigDecimal averageScore;
}