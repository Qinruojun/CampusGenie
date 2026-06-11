package com.genie.mapper;

import com.genie.entity.Category;
import com.genie.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    Integer getIdByName(String name);
    List<CategoryVO> getCategoryList();
    Category selectById(Integer categoryId);

}
