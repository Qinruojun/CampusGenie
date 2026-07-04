package com.genie.service.impl;

import com.genie.constant.JwtClaimsConstant;
import com.genie.constant.StatusConstant;
import com.genie.dto.PasswordChangeDTO;
import com.genie.dto.RegisterDTO;
import com.genie.entity.User;
import com.genie.exception.AccountLockedException;
import com.genie.exception.LoginFailedException;
import com.genie.exception.RegisterFailedException;
import com.genie.exception.PasswordErrorException;
import com.genie.mapper.UserMapper;
import com.genie.properties.JwtProperties;
import com.genie.service.UserService;
import com.genie.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.genie.dto.LoginDTO;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.genie.constant.MessageConstant;
import com.genie.vo.LoginVO;
import com.genie.vo.UserInfoVO;
@Service
public class UserServiceImpl  implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void register(RegisterDTO registerDTO) {
        if(userMapper.selectByEmail(registerDTO.getEmail())!=null){
            throw new RegisterFailedException("邮箱已存在");
        }
        if (userMapper.selectByPhone(registerDTO.getPhone())!=null){
            throw new RegisterFailedException("手机号已存在");
        }
        if (userMapper.selectByUserName(registerDTO.getUsername())!=null){
            throw new RegisterFailedException("用户名已存在");
        }
        User user=new User();
        BeanUtils.copyProperties(registerDTO,user);
        user.setRole(0);
        user.setStatus(1);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.insert(user);
    }
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();

        // ========== 检查账号是否被锁定 ==========
        loginAttemptService.checkLocked(username);
        //进行用户查找获得用户信息，找不到报错
        User user=userMapper.selectByUserName(loginDTO.getUsername());
        if (user==null){
            // ========== 用户名不存在也记录失败（防止枚举攻击） ==========
            loginAttemptService.recordFailure(username);
            throw new LoginFailedException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //密码不正确
        if (!user.getPassword().equals(loginDTO.getPassword())){
            // ========== 新增：密码错误记录失败次数，返回剩余尝试次数 ==========
            long remaining = loginAttemptService.recordFailure(username);
            if (remaining == 0) {
                throw new AccountLockedException("密码错误次数过多，账号已锁定15分钟");
            }
            throw new LoginFailedException("密码错误，剩余尝试次数：" + remaining);
        }
        //身份是否正确
        if(!user.getRole().equals(0)){
            throw new LoginFailedException(MessageConstant.IDENTITY_ERROR);
        }
        //账号是否正常
        if (!StatusConstant.ENABLE.equals(user.getStatus())){
            throw new LoginFailedException(MessageConstant.ACCOUNT_LOCKED);
        }
        loginAttemptService.clearFailure(username);

        //创造jwt
        Map<String,Object> claims=new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        claims.put(JwtClaimsConstant.USERNAME,user.getUsername());
        claims.put(JwtClaimsConstant.ROLE,user.getRole());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        //构造视图对象并返回给前端
        LoginVO loginVO = LoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .token(token)
                .build();
        //修改最后登陆时间
        userMapper.updateLastLoginTime(user.getId(),LocalDateTime.now());
        return  loginVO;
    }

    @Override
    public UserInfoVO getInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new LoginFailedException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdTime(user.getCreatedTime())
                .build();
    }

    @Override
    public void updateInfo(Long userId, String username, String email, String phone) {
        // 如果传了用户名且与原用户名不同，检查是否已被其他用户使用
        if (username != null && !username.trim().isEmpty()) {
            User current = userMapper.selectById(userId);
            if (current != null && !username.equals(current.getUsername())) {
                int count = userMapper.countByUsernameExcludeId(username, userId);
                if (count > 0) {
                    throw new RegisterFailedException("用户名已存在");
                }
            }
            userMapper.updateInfoWithUsername(userId, username, email, phone);
        } else {
            userMapper.updateInfo(userId, email, phone);
        }
    }

    @Override
    public void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO) {
        // 验证旧密码
        String oldPassword = userMapper.selectPasswordById(userId);
        if (oldPassword == null || !oldPassword.equals(passwordChangeDTO.getOldPassword())) {
            throw new PasswordErrorException("旧密码错误");
        }
        // 验证两次新密码一致
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            throw new PasswordErrorException("两次输入的新密码不一致");
        }
        // 更新密码
        userMapper.updatePassword(userId, passwordChangeDTO.getNewPassword());
    }
}
