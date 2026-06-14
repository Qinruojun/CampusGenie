package com.genie.service;

import com.genie.dto.QaMessageSendDTO;
import com.genie.dto.QaConversationRenameDTO;
import com.genie.vo.AnswerVO;
import com.genie.vo.QaConversationVO;
import com.genie.vo.QaMessageVO;

import java.util.List;

public interface QaHistoryService {
    List<QaConversationVO> listConversations();

    List<QaMessageVO> listMessages(Long conversationId);

    QaConversationVO createConversation();

    AnswerVO sendMessage(Long conversationId, QaMessageSendDTO qaMessageSendDTO);

    void deleteConversation(Long conversationId);

    void renameConversation(Long conversationId, QaConversationRenameDTO qaConversationRenameDTO);
}
