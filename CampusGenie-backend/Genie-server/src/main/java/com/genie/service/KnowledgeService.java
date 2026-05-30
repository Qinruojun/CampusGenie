package com.genie.service;

import com.genie.dto.KnowledgeDTO;

public interface KnowledgeService {
    //返回值还不确定，后面再改
    void newKnowledge(KnowledgeDTO knowledgeDTO);//新增知识条目
    void editKnowledge(KnowledgeDTO knowledgeDTO);//编辑知识条目
    void deleteKnowledge(long id);//删除知识条目
    void enableKnowledge(long id);//启用知识条目
    void disableKnowledge(long id);//停用知识条目

}
