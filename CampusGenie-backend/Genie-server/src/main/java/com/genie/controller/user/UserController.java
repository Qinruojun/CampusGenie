package com.genie.controller.user;


import com.genie.constant.CodeConstant;
import com.genie.dto.RegisterDTO;
import com.genie.dto.LoginDTO;
import com.genie.result.Result;
import com.genie.service.UserService;
import com.genie.vo.LoginVO;
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
        userService.register(registerDTO);//调用service层的注册方法
        return Result.success(null, CodeConstant.SUCCESS,"注册成功");
    }
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO){
        log.info("用户进行登录{}", loginDTO);
        LoginVO loginVO =userService.login(loginDTO);//调用service层的登录方法
        return Result.success(loginVO, CodeConstant.SUCCESS,"登录成功");//返回包含用户token的loginVO给前端
    }

}
