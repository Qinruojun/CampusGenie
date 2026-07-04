package com.genie.service;
import com.genie.dto.LoginDTO;
import com.genie.entity.User;
import com.genie.vo.LoginVO;
import jakarta.validation.Valid;

public interface AdminService {

    LoginVO login(@Valid LoginDTO loginDTO);

    User getAdminInfo(Long id);

    void updateAdminInfo(Long id, String email, String phone);

    void changePassword(Long id, String oldPassword, String newPassword);
}
