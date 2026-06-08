package com.school.student_management.repository;

import com.school.student_management.entity.GradeSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface GradeSlipRepository extends JpaRepository<GradeSlip, Long> {
    List<GradeSlip> findByStudentId(Long studentId);
    List<GradeSlip> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
    List<GradeSlip> findBySubjectId(Long subjectId);

    @Query("SELECT DISTINCT gs FROM GradeSlip gs JOIN gs.student s " +
           "WHERE s.classroom.homeroomTeacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username) " +
           "OR gs.subject.teacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username)")
    List<GradeSlip> findByTeacherUsername(@Param("username") String username);

    @Query("SELECT DISTINCT gs FROM GradeSlip gs JOIN gs.student s JOIN s.parents p JOIN UserAccount u ON u.parent.id = p.id WHERE u.username = :username")
    List<GradeSlip> findByParentUsername(@Param("username") String username);
}
