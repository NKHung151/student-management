package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "staffs")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String phone;

    @Column(unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StaffRole role;  // TEACHER, ADMIN, EDUCATION_OFFICER

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @OneToOne(mappedBy = "staff", cascade = CascadeType.ALL)
    private UserAccount userAccount;

    public enum StaffRole {
        TEACHER, ADMIN, EDUCATION_OFFICER
    }
}