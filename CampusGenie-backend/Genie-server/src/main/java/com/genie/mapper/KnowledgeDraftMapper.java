package com.genie.mapper;

import com.genie.dto.KnowledgeDraftPageQueryDTO;
import com.genie.entity.KnowledgeDraft;
import com.genie.vo.KnowledgeDraftVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeDraftMapper {
    Integer insert(KnowledgeDraft knowledgeDraft);

     KnowledgeDraft selectById(Long id);

    void deleteById(Long id);

    void update(KnowledgeDraft knowledgeDraft);

    Page<KnowledgeDraftVO> pageQuery(KnowledgeDraftPageQueryDTO query);
}
