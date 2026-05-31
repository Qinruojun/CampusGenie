package com.genie.service.impl;

import com.genie.dto.RegisterDTO;
import com.genie.entity.User;
import com.genie.exception.LoginFailedException;
import com.genie.exception.RegisterFailedException;
import com.genie.mapper.UserMapper;
import com.genie.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.genie.dto.LoginDTO;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.genie.constant.MessageConstant;
import com.genie.vo.LoginVO;
@Service
public class UserServiceImpl  implements UserService {
    @Autowired
    private UserMapper userMapper;

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
        //进行用户查找获得用户信息，找不到报错
        User user=userMapper.selectByUserName(loginDTO.getUsername());
        if (user==null){
            throw new LoginFailedException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //密码不正确
        if (!user.getPassword().equals(loginDTO.getPassword())){
            throw new LoginFailedException(MessageConstant.PASSWORD_ERROR);
        }

//        log.info("用户登录：{}", userLoginDTO);
//        User user = userService.login(userLoginDTO);
//        Map<String, Object> claims = new HashMap<>();
//        claims.put(JwtClaimsConstant.USER_ID, user.getId());
//
//        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
//        UserLoginVO userLoginVO = UserLoginVO.builder()
//                .id(user.getId())
//                .openid(user.getOpenid())
//                .token(token)
//                .build();

       //还要验证密码是否匹配
        return new LoginVO();
    }
}
