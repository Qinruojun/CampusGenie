package com.genie.service;
import com.genie.dto.LoginDTO;
import com.genie.vo.LoginVO;
import jakarta.validation.Valid;

public interface AdminService {

    LoginVO login(@Valid LoginDTO loginDTO);
}
