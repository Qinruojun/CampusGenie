package com.genie.mapper;

import com.genie.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
//MyBatis 三个作用：执行SQL，封装结果，映射成 Java 对象

    @Select( "select id, username, email, phone from `user` where username = #{username}")
    User selectByUserName(String username);//这里把查到的对象分装成user对象返回
    @Select("select id, username, email, phone from `user` where email=#{email}")
    User selectByEmail(String email);
    @Select("SELECT id, username, email, phone from `user` where phone = #{phone}")
    User selectByPhone(String phone);

    @Select("select id, username, password, email, phone, role, status, " +
            "last_login_time as lastLoginTime, created_time as createdTime, updated_time as updatedTime " +
            "from `user` where username = #{username}")
    User selectLoginUserByUsername(String username);

    @Insert("insert into `user`(username, password, email, phone, role, status, created_time, updated_time) " +
            "values(#{username}, #{password}, #{email}, #{phone}, #{role}, #{status}, #{createdTime}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    // useGeneratedKeys = true,使用数据库自动生成的主键 keyProperty = "id"，把主键赋值给user对象的id属性
    void insert(User user);

    @Update("update `user` set last_login_time = #{lastLoginTime}, updated_time = #{updatedTime} where id = #{id}")
    void updateLoginTime(User user);
}
