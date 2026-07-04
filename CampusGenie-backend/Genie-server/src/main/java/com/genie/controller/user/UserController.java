package com.genie.controller.user;

import com.genie.constant.CodeConstant;
import com.genie.constant.JwtClaimsConstant;
import com.genie.dto.RegisterDTO;
import com.genie.dto.LoginDTO;
import com.genie.dto.PasswordChangeDTO;
import com.genie.properties.JwtProperties;
import com.genie.result.Result;
import com.genie.service.UserService;
import com.genie.utils.JwtUtil;
import com.genie.vo.LoginVO;
import com.genie.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("用户进行注册{}", registerDTO);
        userService.register(registerDTO);
        return Result.success(null, CodeConstant.SUCCESS,"注册成功");
    }
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginDTO loginDTO){
        log.info("用户进行登录{}", loginDTO);
        LoginVO loginVO =userService.login(loginDTO);
        return Result.success(loginVO, CodeConstant.SUCCESS,"用户登录成功");
    }

    @GetMapping("/info")
    public Result<UserInfoVO> info(HttpServletRequest request) {
        Long userId = getUserId(request);
        log.info("获取用户信息 userId={}", userId);
        UserInfoVO userInfo = userService.getInfo(userId);
        return Result.success(userInfo);
    }

    @PutMapping("/info")
    public Result updateInfo(HttpServletRequest request, @RequestBody Map<String, String> body) {
        Long userId = getUserId(request);
        String username = body.get("username");
        String email = body.getOrDefault("email", "");
        String phone = body.getOrDefault("phone", "");
        log.info("更新用户信息 userId={}, username={}, email={}, phone={}", userId, username, email, phone);
        userService.updateInfo(userId, username, email, phone);
        return Result.success(null, CodeConstant.SUCCESS, "更新成功");
    }

    @PutMapping("/password")
    public Result changePassword(HttpServletRequest request, @RequestBody PasswordChangeDTO passwordChangeDTO) {
        Long userId = getUserId(request);
        log.info("用户修改密码 userId={}", userId);
        userService.changePassword(userId, passwordChangeDTO);
        return Result.success(null, CodeConstant.SUCCESS, "密码修改成功");
    }

    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> claims = JwtUtil.parseJWT(
                jwtProperties.getUserSecretKey(),
                token
        );
        Object idObj = claims.get(JwtClaimsConstant.USER_ID);
        if (idObj instanceof Integer) {
            return ((Integer) idObj).longValue();
        }
        return (Long) idObj;
    }
}
