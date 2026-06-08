package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_accounts")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserRole role;   // ADMIN, TEACHER, PARENT

    private boolean enabled = true;

    @OneToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    public enum UserRole {
        ADMIN, TEACHER, EDUCATION_OFFICER, PARENT
    }
}