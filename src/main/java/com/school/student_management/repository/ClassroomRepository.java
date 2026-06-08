package com.school.student_management.repository;

import com.school.student_management.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findBySchoolId(Long schoolId);
    List<Classroom> findBySchoolIdAndSchoolYear(Long schoolId, String schoolYear);
    List<Classroom> findByHomeroomTeacherId(Long teacherId);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c FROM Classroom c " +
           "WHERE c.homeroomTeacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username) " +
           "OR c.id IN (SELECT DISTINCT j.classroom.id FROM ClassJournal j WHERE j.teacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username)) " +
           "OR c.id IN (SELECT DISTINCT sr.student.classroom.id FROM ScoreRecord sr WHERE sr.subject.teacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username))")
    List<Classroom> findByTeacherUsername(@org.springframework.data.repository.query.Param("username") String username);
}