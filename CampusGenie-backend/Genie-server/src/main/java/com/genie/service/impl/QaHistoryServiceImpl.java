package com.genie.service.impl;

import com.genie.context.BaseContext;
import com.genie.dto.AskRequestDTO;
import com.genie.dto.QaConversationRenameDTO;
import com.genie.dto.QaMessageSendDTO;
import com.genie.entity.QaConversation;
import com.genie.entity.QaMessage;
import com.genie.exception.BaseException;
import com.genie.exception.UserNotLoginException;
import com.genie.mapper.QaConversationMapper;
import com.genie.mapper.QaMessageMapper;
import com.genie.service.QAService;
import com.genie.service.QaHistoryService;
import com.genie.vo.AnswerVO;
import com.genie.vo.QaConversationVO;
import com.genie.vo.QaMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QaHistoryServiceImpl implements QaHistoryService {
    @Autowired
    private QaConversationMapper qaConversationMapper;

    @Autowired
    private QaMessageMapper qaMessageMapper;

    @Autowired
    private QAService qaService;

    @Override
    public List<QaConversationVO> listConversations() {
        Long userId = getRequiredUserId();
        return qaConversationMapper.selectByUserId(userId);
    }

    @Override
    public List<QaMessageVO> listMessages(Long conversationId) {
        Long userId = getRequiredUserId();
        ensureConversationOwnedByUser(conversationId, userId);
        return qaMessageMapper.selectByConversationIdAndUserId(conversationId, userId);
    }

    @Override
    public QaConversationVO createConversation() {
        Long userId = getRequiredUserId();
        LocalDateTime now = LocalDateTime.now();

        QaConversation conversation = new QaConversation();
        conversation.setUserId(userId);
        conversation.setTitle("新对话");
        conversation.setCreatedTime(now);
        conversation.setUpdatedTime(now);
        conversation.setDeleted(0);
        qaConversationMapper.insert(conversation);

        QaConversationVO vo = new QaConversationVO();
        vo.setId(conversation.getId());
        vo.setTitle(conversation.getTitle());
        vo.setCreatedTime(now);
        vo.setUpdatedTime(now);
        return vo;
    }

    @Override
    @Transactional
    public AnswerVO sendMessage(Long conversationId, QaMessageSendDTO qaMessageSendDTO) {
        Long userId = getRequiredUserId();
        ensureConversationOwnedByUser(conversationId, userId);

        String question = qaMessageSendDTO.getQuestion().trim();
        LocalDateTime now = LocalDateTime.now();
        Integer messageCount = qaMessageMapper.countByConversationIdAndUserId(conversationId, userId);

        insertMessage(conversationId, userId, "user", question, now);

        AskRequestDTO askRequestDTO = new AskRequestDTO();
        askRequestDTO.setQuestion(question);
        AnswerVO answerVO = qaService.getAnswer(askRequestDTO);
        String answer = answerVO.getAnswer() == null ? "抱歉，暂时没有获取到回答。" : answerVO.getAnswer();

        insertMessage(conversationId, userId, "assistant", answer, LocalDateTime.now());

        if (messageCount == null || messageCount == 0) {
            qaConversationMapper.updateTitleAndTime(conversationId, userId, buildTitle(question), LocalDateTime.now());
        } else {
            qaConversationMapper.updateTime(conversationId, userId, LocalDateTime.now());
        }

        return answerVO;
    }

    @Override
    public void deleteConversation(Long conversationId) {
        Long userId = getRequiredUserId();
        ensureConversationOwnedByUser(conversationId, userId);
        qaConversationMapper.softDeleteByIdAndUserId(conversationId, userId, LocalDateTime.now());
    }

    @Override
    public void renameConversation(Long conversationId, QaConversationRenameDTO qaConversationRenameDTO) {
        Long userId = getRequiredUserId();
        ensureConversationOwnedByUser(conversationId, userId);
        String title = qaConversationRenameDTO.getTitle().trim();
        if (title.isEmpty()) {
            throw new BaseException("对话标题不能为空");
        }
        qaConversationMapper.updateTitle(conversationId, userId, title);
    }

    private Long getRequiredUserId() {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new UserNotLoginException("请先登录后再查看历史对话");
        }
        return userId;
    }

    private void ensureConversationOwnedByUser(Long conversationId, Long userId) {
        if (conversationId == null || qaConversationMapper.selectByIdAndUserId(conversationId, userId) == null) {
            throw new BaseException("对话不存在或无权限访问");
        }
    }

    private void insertMessage(Long conversationId, Long userId, String role, String content, LocalDateTime createdTime) {
        QaMessage message = new QaMessage();
        message.setConversationId(conversationId);
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedTime(createdTime);
        qaMessageMapper.insert(message);
    }

    private String buildTitle(String question) {
        return question.length() > 24 ? question.substring(0, 24) : question;
    }
}
