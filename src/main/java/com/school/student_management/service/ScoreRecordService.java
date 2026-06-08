package com.school.student_management.service;

import com.school.student_management.dto.ScoreRecordDTO;
import java.util.List;

public interface ScoreRecordService {
    List<ScoreRecordDTO> getAllScores();
    ScoreRecordDTO getScoreById(Long id);
    ScoreRecordDTO createScore(ScoreRecordDTO dto);
    ScoreRecordDTO updateScore(Long id, ScoreRecordDTO dto);
    void deleteScore(Long id);
    List<ScoreRecordDTO> getScoresByStudent(Long studentId);
    List<ScoreRecordDTO> getScoresByParent(String username);
    List<ScoreRecordDTO> getScoresByTeacher(String username);
}
