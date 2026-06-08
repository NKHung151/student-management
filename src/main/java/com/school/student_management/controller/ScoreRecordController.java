package com.school.student_management.controller;

import com.school.student_management.dto.ScoreRecordDTO;
import com.school.student_management.service.ScoreRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/score-records")
@RequiredArgsConstructor
public class ScoreRecordController {

    private final ScoreRecordService scoreRecordService;

    @GetMapping
    public ResponseEntity<List<ScoreRecordDTO>> getAllScores(Authentication authentication) {
        boolean isParent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARENT"));
        boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        if (isParent) {
            String username = authentication.getName();
            return ResponseEntity.ok(scoreRecordService.getScoresByParent(username));
        }
        if (isTeacher) {
            String username = authentication.getName();
            return ResponseEntity.ok(scoreRecordService.getScoresByTeacher(username));
        }
        return ResponseEntity.ok(scoreRecordService.getAllScores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScoreRecordDTO> getScoreById(@PathVariable Long id) {
        return ResponseEntity.ok(scoreRecordService.getScoreById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ScoreRecordDTO>> getScoresByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(scoreRecordService.getScoresByStudent(studentId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'EDUCATION_OFFICER')")
    public ResponseEntity<ScoreRecordDTO> createScore(@Valid @RequestBody ScoreRecordDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scoreRecordService.createScore(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'EDUCATION_OFFICER')")
    public ResponseEntity<ScoreRecordDTO> updateScore(@PathVariable Long id, @Valid @RequestBody ScoreRecordDTO dto) {
        return ResponseEntity.ok(scoreRecordService.updateScore(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteScore(@PathVariable Long id) {
        scoreRecordService.deleteScore(id);
        return ResponseEntity.noContent().build();
    }
}
