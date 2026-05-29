package com.genie.controller.user;


import com.genie.constant.CodeConstant;
import com.genie.dto.RegisterDTO;
import com.genie.result.Result;
import com.genie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        log.info("用户进行注册{}", registerDTO);
        userService.register(registerDTO);
        return Result.success(null, CodeConstant.SUCCESS,"注册成功");
    }
}
