package com.genie.mapper;

import com.genie.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {


    @Select( "select * from user where username = #{username}")
    User selectByUserName(String username);
    @Select("select * from user where email=#{email}")
    User selectByEmail(String email);
    @Select("SELECT * from user where phone = #{phone}")
    User selectByPhone(String phone);
    void insert(User user);
}
