package com.genie.mapper;

import com.genie.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {


    @Select("select id, username, email, phone,role,password,status from user where username = #{username}")
    User selectByUserName(String username);

    @Select("select id, username, email, phone from user where email=#{email}")
    User selectByEmail(String email);

    @Select("SELECT id, username, email, phone from user where phone = #{phone}")
    User selectByPhone(String phone);

    void insert(User user);

    void updateLastLoginTime(Long id, LocalDateTime lastLoginTime);

    @Select("select id, username, email, phone, created_time from user where id = #{id}")
    User selectById(Long id);

    @Select("update user set email = #{email}, phone = #{phone} where id = #{id}")
    void updateInfo(Long id, String email, String phone);

    @Select("update user set password = #{password} where id = #{id}")
    void updatePassword(Long id, String password);

    @Select("select password from user where id = #{id}")
    String selectPasswordById(Long id);
}
