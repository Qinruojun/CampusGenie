package com.genie.service.impl;

import com.genie.constant.JwtClaimsConstant;
import com.genie.constant.MessageConstant;
import com.genie.dto.LoginDTO;
import com.genie.entity.User;
import com.genie.exception.LoginFailedException;
import com.genie.mapper.UserMapper;
import com.genie.properties.JwtProperties;
import com.genie.service.AdminService;
import com.genie.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.genie.vo.LoginVO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl  implements AdminService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProperties jwtProperties;
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
        //身份判断
        if(user.getRole()!=1){
            throw new LoginFailedException(MessageConstant.IDENTITY_ERROR);
        }
        //创造jwt
        Map<String,Object> claims=new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        claims.put(JwtClaimsConstant.USERNAME,user.getUsername());
        claims.put(JwtClaimsConstant.ROLE,user.getRole());
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);
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
        userMapper.updateLastLoginTime(user.getId(), LocalDateTime.now());
        return  loginVO;
    }

}
