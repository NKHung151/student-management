package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "subjects")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Staff teacher;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<ScoreRecord> scoreRecords;
}