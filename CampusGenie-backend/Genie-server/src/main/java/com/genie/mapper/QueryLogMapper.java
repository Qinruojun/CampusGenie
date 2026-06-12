package com.genie.mapper;

import com.genie.entity.HotQuestion;
import com.genie.entity.QueryLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface QueryLogMapper {
    void insert(QueryLog queryLog);

    // 根据一天前到现在的有效用户查询构造热点问题列表
    List<HotQuestion> getQueryLogsByTime_to_HotQuestion(@Param("startDate")LocalDateTime localDateTime,@Param("endDate") LocalDateTime now);
}
