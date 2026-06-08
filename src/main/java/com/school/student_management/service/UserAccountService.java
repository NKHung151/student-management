package com.school.student_management.service;

import com.school.student_management.dto.UserAccountDTO;
import java.util.List;

public interface UserAccountService {
    List<UserAccountDTO> getAllAccounts();
    UserAccountDTO getAccountById(Long id);
    UserAccountDTO createAccount(UserAccountDTO dto);
    void changePassword(Long id, String oldPassword, String newPassword);
    void deleteAccount(Long id);
}
