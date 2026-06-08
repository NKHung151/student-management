package com.school.student_management.repository;

import com.school.student_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByClassroomId(Long classroomId);

    List<Student> findByFullNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM Student s WHERE s.classroom.school.id = :schoolId")
    List<Student> findBySchoolId(@Param("schoolId") Long schoolId);

    @Query("SELECT s FROM Student s JOIN s.parents p JOIN UserAccount u ON u.parent.id = p.id WHERE u.username = :username")
    List<Student> findByParentUsername(@Param("username") String username);

    @Query("SELECT s FROM Student s JOIN s.parents p JOIN UserAccount u ON u.parent.id = p.id WHERE u.username = :username")
    Page<Student> findByParentUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Student s JOIN s.classroom c " +
           "WHERE c.homeroomTeacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username) " +
           "OR c.id IN (SELECT DISTINCT j.classroom.id FROM ClassJournal j WHERE j.teacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username)) " +
           "OR c.id IN (SELECT DISTINCT sr.student.classroom.id FROM ScoreRecord sr WHERE sr.subject.teacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username))")
    List<Student> findByTeacherUsername(@Param("username") String username);

    @Query("SELECT DISTINCT s FROM Student s JOIN s.classroom c " +
           "WHERE c.homeroomTeacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username) " +
           "OR c.id IN (SELECT DISTINCT j.classroom.id FROM ClassJournal j WHERE j.teacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username)) " +
           "OR c.id IN (SELECT DISTINCT sr.student.classroom.id FROM ScoreRecord sr WHERE sr.subject.teacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username))")
    Page<Student> findByTeacherUsername(@Param("username") String username, Pageable pageable);

    boolean existsByStudentCode(String studentCode);
}