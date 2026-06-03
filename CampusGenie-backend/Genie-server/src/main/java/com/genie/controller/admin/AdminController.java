package com.genie.controller.admin;


import com.genie.constant.CodeConstant;
import com.genie.dto.LoginDTO;
import com.genie.result.Result;
import com.genie.service.AdminService;
import com.genie.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private AdminService adminService;
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginDTO loginDTO){
        log.info("管理员进行登录{}", loginDTO);
        LoginVO loginVO =adminService.login(loginDTO);//调用service层的登录方法
        return Result.success(loginVO, CodeConstant.SUCCESS,"管理员登录成功");//返回包含用户token的loginVO给前端
    }
}
