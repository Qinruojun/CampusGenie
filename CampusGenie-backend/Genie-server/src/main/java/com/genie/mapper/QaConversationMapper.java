package com.genie.mapper;

import com.genie.entity.QaConversation;
import com.genie.vo.QaConversationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface QaConversationMapper {
    void insert(QaConversation qaConversation);

    QaConversation selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    List<QaConversationVO> selectByUserId(Long userId);

    void updateTitleAndTime(@Param("id") Long id,
                            @Param("userId") Long userId,
                            @Param("title") String title,
                            @Param("updatedTime") LocalDateTime updatedTime);

    void updateTime(@Param("id") Long id,
                    @Param("userId") Long userId,
                    @Param("updatedTime") LocalDateTime updatedTime);
}
