package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "classrooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;           // Ví dụ: "10A1"

    @Column(name = "grade_level")
    private Integer gradeLevel;    // 10, 11, 12

    @Column(name = "school_year", length = 20)
    private String schoolYear;     // "2024-2025"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homeroom_teacher_id")
    private Staff homeroomTeacher;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> students;
}