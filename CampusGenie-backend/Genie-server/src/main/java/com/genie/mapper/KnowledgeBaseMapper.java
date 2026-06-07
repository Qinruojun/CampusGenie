package com.genie.mapper;

import com.genie.dto.KnowledgePageQueryDTO;
import com.genie.entity.KnowledgeBase;
import com.genie.vo.KnowledgeVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KnowledgeBaseMapper {
    void insert(KnowledgeBase knowledgeBase);

    void update(KnowledgeBase knowledgeBase);

    KnowledgeBase selectById(Long id);

    void deleteById(Long id);


    KnowledgeBase selectByIdAndStatus(Long id, Integer status);

    Page<KnowledgeVO> pageQuery(KnowledgePageQueryDTO knowledgePageQueryDTO);

    List<KnowledgeBase> selectByKeyword_CategoryId_Status(String keyword, Integer categoryId, Integer status);
}
