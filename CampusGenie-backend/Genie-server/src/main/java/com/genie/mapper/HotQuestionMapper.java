package com.genie.mapper;

import com.genie.entity.HotQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HotQuestionMapper {
    Integer getMaxVersion();
    
    void insertBatch(@Param("hotQuestions") java.util.List<com.genie.entity.HotQuestion> hotQuestions);

    List<HotQuestion> selectList(Integer maxVersion);

    Integer selectByKnowledgeId_hitPlace_version(Long knowledgeId, Integer hitPlace, int version);
}
