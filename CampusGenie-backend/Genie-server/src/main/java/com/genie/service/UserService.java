package com.genie.service;

import com.genie.dto.AskRequestDTO;
import com.genie.dto.ContributionSubmitDTO;
import com.genie.dto.LoginDTO;
import com.genie.dto.RegisterDTO;
import com.genie.dto.PasswordChangeDTO;
import com.genie.vo.AnswerVO;
import com.genie.vo.LoginVO;
import com.genie.vo.UserContributionVO;
import com.genie.vo.UserInfoVO;

public interface UserService {
    void register(RegisterDTO registerDTO);
    LoginVO login(LoginDTO loginDTO);

    UserInfoVO getInfo(Long userId);
    void updateInfo(Long userId, String username, String email, String phone);
    void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO);
}
