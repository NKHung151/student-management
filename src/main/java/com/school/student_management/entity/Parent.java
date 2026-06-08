package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "parents")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String occupation;

    @Column(length = 500)
    private String address;

    @ManyToMany(mappedBy = "parents")
    private List<Student> students;
}