package com.genie.service.impl;

import com.genie.constant.JwtClaimsConstant;
import com.genie.constant.StatusConstant;
import com.genie.dto.RegisterDTO;
import com.genie.entity.User;
import com.genie.exception.LoginFailedException;
import com.genie.exception.RegisterFailedException;
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
import java.util.Objects;

import com.genie.constant.MessageConstant;
import com.genie.vo.LoginVO;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl  implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional//任何一步报错就撤销所有操作
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
//将registerDTO中的数据复制到User entity实例user中
        BeanUtils.copyProperties(registerDTO,user);
        user.setRole(StatusConstant.USER_ROLE);
        user.setStatus(StatusConstant.USER_NORMAL);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.insert(user);//插入数据库
    }


    @Autowired
    private JwtProperties jwtProperties;
    @Override
    @Transactional
    public LoginVO login(LoginDTO loginDTO) {

        User user=userMapper.selectLoginUserByUsername(loginDTO.getUsername());
        //查询用户名，判断账号是否存在
        if (user==null){
            throw new LoginFailedException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //校验密码是否匹配
        if (!user.getPassword().equals(loginDTO.getPassword())){
            throw new LoginFailedException(MessageConstant.PASSWORD_ERROR);
        }
        //判断用户是否被禁用
        if (Objects.equals(user.getStatus(), StatusConstant.USER_BANNED)){
            throw new LoginFailedException(MessageConstant.ACCOUNT_LOCKED);
        }
        LoginVO loginVO=new LoginVO();
        // 4. 生成 JWT claims
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        claims.put(JwtClaimsConstant.USERNAME, user.getUsername());
        claims.put(JwtClaimsConstant.ROLE, user.getRole());

        // 5. 生成 token
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );
        // 6. 更新最后登录时间
        LocalDateTime now = LocalDateTime.now();
        user.setLastLoginTime(now);
        user.setUpdatedTime(now);
        userMapper.updateLoginTime(user);

        // 7. 封装返回结果

        loginVO.setId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setEmail(user.getEmail());
        loginVO.setPhone(user.getPhone());
        loginVO.setRole(user.getRole());
        loginVO.setToken(token);

        return loginVO;

    }
}
