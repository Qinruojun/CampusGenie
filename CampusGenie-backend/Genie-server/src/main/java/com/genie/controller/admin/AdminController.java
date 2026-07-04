package com.genie.controller.admin;


import com.genie.constant.CodeConstant;
import com.genie.context.BaseContext;
import com.genie.context.UserContext;
import com.genie.dto.LoginDTO;
import com.genie.entity.User;
import com.genie.result.Result;
import com.genie.service.AdminService;
import com.genie.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("/info")
    public Result<User> getAdminInfo() {
        UserContext userContext = BaseContext.getCurrentUserContext();
        User user = adminService.getAdminInfo(userContext.getUserId());
        return Result.success(user);
    }

    @PutMapping("/info")
    public Result updateAdminInfo(@RequestBody Map<String, String> params) {
        UserContext userContext = BaseContext.getCurrentUserContext();
        adminService.updateAdminInfo(userContext.getUserId(), params.get("email"), params.get("phone"));
        return Result.success(null, CodeConstant.SUCCESS, "更新成功");
    }

    @PutMapping("/password")
    public Result changePassword(@RequestBody Map<String, String> params) {
        UserContext userContext = BaseContext.getCurrentUserContext();
        adminService.changePassword(userContext.getUserId(), params.get("oldPassword"), params.get("newPassword"));
        return Result.success(null, CodeConstant.SUCCESS, "密码修改成功");
    }
}
