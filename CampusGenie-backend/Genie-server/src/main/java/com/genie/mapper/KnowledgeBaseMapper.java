package com.genie.mapper;

import com.genie.entity.KnowledgeBase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeBaseMapper {
    void insert(KnowledgeBase knowledgeBase);
}
