package com.genie.controller.admin;


import com.genie.constant.CodeConstant;
import com.genie.dto.KnowledgeDTO;
import com.genie.result.Result;
import com.genie.service.KnowledgeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/knowledge")
@Slf4j
public class KnowledgeController {
    @Autowired
    private KnowledgeService knowledgeService;

    @PostMapping
    public Result addKnowledge(@Valid @RequestBody KnowledgeDTO knowledgeDTO) {
        log.info("添加知识条目{}");
        knowledgeService.addKnowledge(knowledgeDTO);
        return Result.success(null, CodeConstant.SUCCESS, "添加成功");
    }
}
