package com.genie.mapper;

import com.genie.entity.ReviewLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewLogMapper {
    void insert(ReviewLog reviewLog);
}
