package com.genie.mapper;

import com.genie.entity.QaMessage;
import com.genie.vo.QaMessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QaMessageMapper {
    void insert(QaMessage qaMessage);

    Integer countByConversationIdAndUserId(@Param("conversationId") Long conversationId,
                                           @Param("userId") Long userId);

    List<QaMessageVO> selectByConversationIdAndUserId(@Param("conversationId") Long conversationId,
                                                      @Param("userId") Long userId);
}
