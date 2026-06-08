package com.school.student_management.repository;

import com.school.student_management.entity.ReportCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportCardRepository extends JpaRepository<ReportCard, Long> {
    List<ReportCard> findByStudentId(Long studentId);
    Optional<ReportCard> findByStudentIdAndSchoolYear(Long studentId, String schoolYear);

    @Query("SELECT rc FROM ReportCard rc JOIN rc.student s JOIN s.parents p JOIN UserAccount u ON u.parent.id = p.id WHERE u.username = :username")
    List<ReportCard> findByParentUsername(@Param("username") String username);

    @Query("SELECT rc FROM ReportCard rc JOIN rc.student s " +
           "WHERE s.classroom.homeroomTeacher.id = (SELECT ua.staff.id FROM UserAccount ua WHERE ua.username = :username)")
    List<ReportCard> findByTeacherUsername(@Param("username") String username);
}
