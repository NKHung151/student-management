package com.school.student_management.repository;

import com.school.student_management.entity.StudentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCardRepository extends JpaRepository<StudentCard, Long> {
    Optional<StudentCard> findByCardNumber(String cardNumber);
    Optional<StudentCard> findByStudentId(Long studentId);

    @Query("SELECT sc FROM StudentCard sc JOIN sc.student s JOIN s.parents p JOIN UserAccount u ON u.parent.id = p.id WHERE u.username = :username")
    List<StudentCard> findByParentUsername(@Param("username") String username);
}
