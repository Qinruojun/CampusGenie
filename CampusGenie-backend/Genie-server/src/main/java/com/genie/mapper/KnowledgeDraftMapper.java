package com.genie.mapper;

import com.genie.dto.KnowledgeDraftPageQueryDTO;
import com.genie.entity.KnowledgeDraft;
import com.genie.vo.KnowledgeDraftVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface KnowledgeDraftMapper {
    Integer insert(KnowledgeDraft knowledgeDraft);

     KnowledgeDraft selectById(Long id);

    void deleteById(Long id);

    void update(KnowledgeDraft knowledgeDraft);

    Page<KnowledgeDraftVO> pageQuery(KnowledgeDraftPageQueryDTO query);

    KnowledgeDraftVO selectDetailById(Long id);

    Integer countByStatus(@Param("status") Integer status);

    Integer countByCreatedTimeAfter(@Param("time") java.time.LocalDateTime time);

    Integer countByCreatedTimeBetween(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    Integer countApprovedByReviewedTimeAfter(@Param("time") java.time.LocalDateTime time);

    Integer countApprovedByReviewedTimeBetween(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);
}
