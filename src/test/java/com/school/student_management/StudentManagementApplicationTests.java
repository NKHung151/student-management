package com.school.student_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class StudentManagementApplicationTests {

    @Test
    void contextLoads() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$e0MYzXy5.h18c5R/4bO04ux5Xm.VwL.7vV91yQ28gG57Q3g4L.WNu";
        boolean match = encoder.matches("123456", hash);
        System.out.println("====== HASH MATCHES: " + match + " ======");
        System.out.println("====== NEW HASH FOR 123456: " + encoder.encode("123456") + " ======");
    }

}
