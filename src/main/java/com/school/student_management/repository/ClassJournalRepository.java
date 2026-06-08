package com.school.student_management.repository;

import com.school.student_management.entity.ClassJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface ClassJournalRepository extends JpaRepository<ClassJournal, Long> {
    List<ClassJournal> findByClassroomId(Long classroomId);
    List<ClassJournal> findByTeacherId(Long teacherId);
    List<ClassJournal> findByClassroomIdAndSubjectId(Long classroomId, Long subjectId);

    @Query("SELECT j FROM ClassJournal j WHERE j.teacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username) " +
           "OR j.classroom.homeroomTeacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username)")
    List<ClassJournal> findByTeacherUsername(@Param("username") String username);
}
