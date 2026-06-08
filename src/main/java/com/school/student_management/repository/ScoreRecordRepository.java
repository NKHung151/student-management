package com.school.student_management.repository;

import com.school.student_management.entity.ScoreRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ScoreRecordRepository extends JpaRepository<ScoreRecord, Long> {
    List<ScoreRecord> findByStudentId(Long studentId);
    List<ScoreRecord> findByStudentIdAndSemesterAndSchoolYear(
        Long studentId, String semester, String schoolYear);

    @Query("SELECT sr FROM ScoreRecord sr JOIN sr.student s JOIN s.parents p JOIN UserAccount u ON u.parent.id = p.id WHERE u.username = :username")
    List<ScoreRecord> findByParentUsername(@Param("username") String username);

    @Query("SELECT DISTINCT sr FROM ScoreRecord sr JOIN sr.student s " +
           "WHERE s.classroom.homeroomTeacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username) " +
           "OR sr.subject.teacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username)")
    List<ScoreRecord> findByTeacherUsername(@Param("username") String username);
}