package com.school.student_management.service;

import com.school.student_management.dto.SchoolDTO;
import java.util.List;

public interface SchoolService {
    List<SchoolDTO> getAllSchools();
    SchoolDTO getSchoolById(Long id);
    SchoolDTO createSchool(SchoolDTO dto);
    SchoolDTO updateSchool(Long id, SchoolDTO dto);
    void deleteSchool(Long id);
}
