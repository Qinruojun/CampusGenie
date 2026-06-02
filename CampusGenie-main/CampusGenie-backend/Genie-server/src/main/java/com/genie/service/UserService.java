package com.genie.service;

import com.genie.dto.AskRequestDTO;
import com.genie.dto.ContributionSubmitDTO;
import com.genie.dto.LoginDTO;
import com.genie.dto.RegisterDTO;
import com.genie.vo.AnswerVO;
import com.genie.vo.LoginVO;
import com.genie.vo.UserContributionVO;

public interface UserService {
    void register(RegisterDTO registerDTO);
    LoginVO login(LoginDTO loginDTO);


}
