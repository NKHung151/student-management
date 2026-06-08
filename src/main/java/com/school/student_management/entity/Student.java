package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 10)
    private String gender;       // MALE, FEMALE

    @Column(length = 500)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(name = "student_code", unique = true, length = 20)
    private String studentCode;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToMany
    @JoinTable(
        name = "student_parents",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "parent_id")
    )
    private List<Parent> parents;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<ScoreRecord> scoreRecords;
}