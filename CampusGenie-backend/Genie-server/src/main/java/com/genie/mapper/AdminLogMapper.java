package com.genie.mapper;

import com.genie.entity.AdminLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminLogMapper {
    /**
     * 增加管理员操作日志
     * @param adminLog
     */
    @Insert("insert into admin_log(admin_name,action_type,target_type,target_id,detail,created_time) values(#{adminName},#{actionType},#{targetType},#{targetId},#{detail},#{createdTime})")
    void insert(AdminLog adminLog);
}
