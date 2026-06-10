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

    //根据一天前到现在的查询记录来构造热点问题列表(根据hit_place和knowledge_id分组，且hit为1)
    List<HotQuestion> getQueryLogsByTime_to_HotQuestion(@Param("startDate")LocalDateTime localDateTime,@Param("endDate") LocalDateTime now);
}
