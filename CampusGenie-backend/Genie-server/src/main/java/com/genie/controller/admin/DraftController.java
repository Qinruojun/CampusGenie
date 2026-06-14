package com.genie.controller.admin;


import com.genie.constant.CodeConstant;
import com.genie.dto.AdminContributionPageQueryDTO;
import com.genie.dto.KnowledgeDTO;
import com.genie.dto.KnowledgeDraftPageQueryDTO;
import com.genie.result.PageResult;
import com.genie.result.Result;
import com.genie.service.KnowledgeDraftService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/admin/knowlegedraft")
public class DraftController {
    @Autowired
    private KnowledgeDraftService knowledgeDraftService;

    @PutMapping("/{id}/approve")
    public Result approve(@PathVariable Long id) {
        knowledgeDraftService.approve(id);
        return Result.success(null, CodeConstant.SUCCESS, "审核通过成功");
    }

    @DeleteMapping("/{id}/reject")
    public Result reject(@PathVariable Long id) {
        knowledgeDraftService.reject(id);
        return Result.success(null, CodeConstant.SUCCESS, "审核删除成功");
    }
    @PutMapping("/edit")//修改知识条目
    public Result editKnowledge(@Valid @RequestBody KnowledgeDTO knowledgeDTO) {
        log.info("修改知识草稿{}", knowledgeDTO);
        knowledgeDraftService.editKnowledge(knowledgeDTO);
        return Result.success(null,CodeConstant.SUCCESS,"修改知识草稿成功");
    }
    @GetMapping("/page")
    public Result pageQuery(@Valid KnowledgeDraftPageQueryDTO knowledgeDraftPageQueryDTO) {
        log.info("分页查询用户贡献信息：{}",knowledgeDraftPageQueryDTO);
        PageResult pageResult = knowledgeDraftService.pageQuery(knowledgeDraftPageQueryDTO);
        return Result.success(pageResult, CodeConstant.SUCCESS, "分页查询成功");
    }

}
