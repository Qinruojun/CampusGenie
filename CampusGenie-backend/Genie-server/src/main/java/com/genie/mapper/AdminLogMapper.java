package com.genie.mapper;

import com.genie.entity.AdminLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminLogMapper {
    @Insert("insert into admin_log(admin_name,action_type,target_type,target_id,detail,created_time) values(#{adminName},#{actionType},#{targetType},#{targetId},#{detail},#{createdTime})")
    void insert(AdminLog adminLog);

    @Select("SELECT * FROM admin_log ORDER BY created_time DESC LIMIT #{offset}, #{limit}")
    List<AdminLog> selectRecent(@Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select("SELECT COUNT(*) FROM admin_log")
    Integer countAll();
}
