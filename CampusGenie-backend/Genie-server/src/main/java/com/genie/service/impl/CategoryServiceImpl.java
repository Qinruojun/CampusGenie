package com.genie.service.impl;

import com.genie.entity.Category;
import com.genie.mapper.CategoryMapper;
import com.genie.service.CategoryService;
import com.genie.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public List<CategoryVO> getCategoryList() {
        return categoryMapper.getCategoryList();
    }

    @Override
    public Integer getIdByName(String cellValue) {
        return  categoryMapper.getIdByName(cellValue);
    }
}
