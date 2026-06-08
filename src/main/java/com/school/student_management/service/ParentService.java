package com.school.student_management.service;

import com.school.student_management.dto.ParentDTO;
import java.util.List;

public interface ParentService {
    List<ParentDTO> getAllParents();
    ParentDTO getParentById(Long id);
    ParentDTO createParent(ParentDTO dto);
    ParentDTO updateParent(Long id, ParentDTO dto);
    void deleteParent(Long id);
}
