package com.school.student_management.service;

import com.school.student_management.dto.StaffDTO;
import java.util.List;

public interface StaffService {
    List<StaffDTO> getAllStaffs();
    StaffDTO getStaffById(Long id);
    StaffDTO createStaff(StaffDTO dto);
    StaffDTO updateStaff(Long id, StaffDTO dto);
    void deleteStaff(Long id);
}
