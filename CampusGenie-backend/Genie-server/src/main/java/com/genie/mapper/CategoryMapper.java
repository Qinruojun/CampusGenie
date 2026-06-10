package com.genie.mapper;

import com.genie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    Integer getIdByName(String name);

    Category selectById(Integer categoryId);
}
