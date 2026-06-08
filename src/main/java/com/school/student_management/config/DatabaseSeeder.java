package com.school.student_management.config;

import com.school.student_management.entity.*;
import com.school.student_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final SchoolRepository schoolRepository;
    private final ClassroomRepository classroomRepository;
    private final StaffRepository staffRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentCardRepository studentCardRepository;
    private final ClassJournalRepository classJournalRepository;
    private final GradeSlipRepository gradeSlipRepository;
    private final ScoreRecordRepository scoreRecordRepository;
    private final ReportCardRepository reportCardRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userAccountRepository.existsByUsername("admin")) {
            return; // Database already seeded
        }

        // 1. School
        School school = School.builder()
                .name("Trường THPT Nguyễn Trãi")
                .address("Ba Đình, Hà Nội")
                .phone("02438234567")
                .email("c3nguyentrai@hanoiedu.vn")
                .schoolType("THPT")
                .build();
        school = schoolRepository.save(school);

        // 2. Staffs
        // Admin Staff
        Staff adminStaff = Staff.builder()
                .fullName("Quản trị viên Hệ thống")
                .email("admin@nguyentrai.edu.vn")
                .phone("0912345678")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .role(Staff.StaffRole.ADMIN)
                .school(school)
                .build();
        adminStaff = staffRepository.save(adminStaff);

        // Education Officer Staff
        Staff officerStaff = Staff.builder()
                .fullName("Lê Thị Giáo Vụ")
                .email("giaovu@nguyentrai.edu.vn")
                .phone("0987654321")
                .dateOfBirth(LocalDate.of(1990, 8, 22))
                .role(Staff.StaffRole.EDUCATION_OFFICER)
                .school(school)
                .build();
        officerStaff = staffRepository.save(officerStaff);

        // Teacher Staff
        Staff teacherStaff = Staff.builder()
                .fullName("Nguyễn Văn Toán")
                .email("toan.nv@nguyentrai.edu.vn")
                .phone("0934567890")
                .dateOfBirth(LocalDate.of(1980, 12, 10))
                .role(Staff.StaffRole.TEACHER)
                .school(school)
                .build();
        teacherStaff = staffRepository.save(teacherStaff);

        // 3. User Accounts
        UserAccount adminAccount = UserAccount.builder()
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                .role(UserAccount.UserRole.ADMIN)
                .enabled(true)
                .staff(adminStaff)
                .build();
        userAccountRepository.save(adminAccount);

        UserAccount officerAccount = UserAccount.builder()
                .username("officer")
                .password(passwordEncoder.encode("123456"))
                .role(UserAccount.UserRole.EDUCATION_OFFICER)
                .enabled(true)
                .staff(officerStaff)
                .build();
        userAccountRepository.save(officerAccount);

        UserAccount teacherAccount = UserAccount.builder()
                .username("teacher")
                .password(passwordEncoder.encode("123456"))
                .role(UserAccount.UserRole.TEACHER)
                .enabled(true)
                .staff(teacherStaff)
                .build();
        userAccountRepository.save(teacherAccount);

        // 4. Classrooms
        Classroom classroom = Classroom.builder()
                .name("10 Toán 1")
                .gradeLevel(10)
                .schoolYear("2025-2026")
                .school(school)
                .homeroomTeacher(teacherStaff)
                .build();
        classroom = classroomRepository.save(classroom);

        // 5. Subjects
        Subject subject = Subject.builder()
                .name("Toán Học")
                .description("Môn học Toán đại số và hình học lớp 10")
                .teacher(teacherStaff)
                .build();
        subject = subjectRepository.save(subject);

        // 6. Parents
        Parent parent = Parent.builder()
                .fullName("Nguyễn Văn Phụ Huynh")
                .phone("0904123456")
                .email("parent@nguyentrai.edu.vn")
                .occupation("Kỹ sư")
                .address("Cầu Giấy, Hà Nội")
                .build();
        parent = parentRepository.save(parent);

        UserAccount parentAccount = UserAccount.builder()
                .username("parent")
                .password(passwordEncoder.encode("123456"))
                .role(UserAccount.UserRole.PARENT)
                .enabled(true)
                .parent(parent)
                .build();
        userAccountRepository.save(parentAccount);

        // 7. Students
        Student student = Student.builder()
                .fullName("Nguyễn Học Sinh A")
                .studentCode("HS10001")
                .dateOfBirth(LocalDate.of(2010, 1, 1))
                .gender("MALE")
                .address("Cầu Giấy, Hà Nội")
                .phone("0955555555")
                .email("studentA@nguyentrai.edu.vn")
                .classroom(classroom)
                .parents(new ArrayList<>(Collections.singletonList(parent)))
                .build();
        student = studentRepository.save(student);

        // Update parent with student relation
        parent.setStudents(new ArrayList<>(Collections.singletonList(student)));
        parentRepository.save(parent);

        // 8. Student Cards
        StudentCard studentCard = StudentCard.builder()
                .cardNumber("CARD-NT-10001")
                .issueDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(3))
                .status("ACTIVE")
                .student(student)
                .build();
        studentCardRepository.save(studentCard);

        // 9. Class Journals
        ClassJournal classJournal = ClassJournal.builder()
                .classroom(classroom)
                .teacher(teacherStaff)
                .subject(subject)
                .teachingDate(LocalDate.now())
                .lessonPeriod(1)
                .lessonContent("Đại số: Mệnh đề toán học và Tập hợp")
                .studentAttendance("Không vắng")
                .remarks("Lớp học nghiêm túc, phát biểu bài sôi nổi")
                .score(10)
                .build();
        classJournalRepository.save(classJournal);

        // 10. Grade Slips
        GradeSlip gradeSlip1 = GradeSlip.builder()
                .student(student)
                .subject(subject)
                .title("Kiểm tra 15 phút lần 1")
                .scoreType("15MIN")
                .score(BigDecimal.valueOf(9.0))
                .examDate(LocalDate.now().minusDays(5))
                .remarks("Làm bài tốt")
                .build();
        gradeSlipRepository.save(gradeSlip1);

        GradeSlip gradeSlip2 = GradeSlip.builder()
                .student(student)
                .subject(subject)
                .title("Kiểm tra 1 tiết lần 1")
                .scoreType("45MIN")
                .score(BigDecimal.valueOf(8.5))
                .examDate(LocalDate.now().minusDays(2))
                .remarks("Có ý thức tự giác")
                .build();
        gradeSlipRepository.save(gradeSlip2);

        // 11. Score Records
        ScoreRecord scoreRecord = ScoreRecord.builder()
                .student(student)
                .subject(subject)
                .semester("HK1")
                .schoolYear("2025-2026")
                .score15min(BigDecimal.valueOf(9.0))
                .score45min(BigDecimal.valueOf(8.5))
                .scoreFinal(BigDecimal.valueOf(9.5))
                .averageScore(BigDecimal.valueOf(9.17)) // (9.0 + 8.5*2 + 9.5*3) / 6 = 55 / 6 = 9.17
                .build();
        scoreRecordRepository.save(scoreRecord);

        // 12. Report Cards
        ReportCard reportCard = ReportCard.builder()
                .student(student)
                .schoolYear("2025-2026")
                .gradeLevel(10)
                .gpa(BigDecimal.valueOf(9.17))
                .conduct("EXCELLENT")
                .teacherRemarks("Học sinh thông minh, chăm chỉ, năng nổ tham gia các hoạt động tập thể.")
                .parentRemarks("Gia đình đồng tình với nhận xét của giáo viên. Sẽ tiếp tục động viên con.")
                .promoted(true)
                .build();
        reportCardRepository.save(reportCard);
    }
}
