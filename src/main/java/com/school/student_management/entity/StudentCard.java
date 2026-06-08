package com.school.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "student_cards")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", unique = true, nullable = false, length = 50)
    private String cardNumber;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(length = 20)
    private String status; // ACTIVE, EXPIRED, LOCKED

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
