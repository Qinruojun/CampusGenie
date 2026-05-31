package com.genie.controller.admin;

import com.genie.constant.CodeConstant;
import com.genie.dto.KnowledgeDTO;
import com.genie.dto.KnowledgePageQueryDTO;
import com.genie.dto.StatusUpdateDTO;
import com.genie.result.PageResult;
import com.genie.result.Result;
import com.genie.service.KnowledgeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/knowledge")
public class KnowledgeController {
    @Autowired private KnowledgeService knowledgeService;
    @PostMapping("/new")//新增知识条目
    public Result newKnowledge(@Valid @RequestBody KnowledgeDTO knowledgeDTO) {
        log.info("新增知识条目{}", knowledgeDTO);
         knowledgeService.newKnowledge(knowledgeDTO);
         return Result.success(null, CodeConstant.SUCCESS,"新增知识条目成功");
    }
    @PutMapping("/edit")//修改知识条目
    public Result editKnowledge(@Valid @RequestBody KnowledgeDTO knowledgeDTO) {
        log.info("修改知识条目{}", knowledgeDTO);
         knowledgeService.editKnowledge(knowledgeDTO);
         return Result.success(null,CodeConstant.SUCCESS,"修改知识条目成功");
    }
    @DeleteMapping("/{id}/delete")//删除知识条目
    public Result deleteKnowledge(@PathVariable Long id) {//将URL中的id取出来传给方法里的id
        log.info("删除知识条目{}", id);
         knowledgeService.deleteKnowledge(id);
         return Result.success(null,CodeConstant.SUCCESS,"删除知识条目成功");
    }
    @PutMapping("/{id}/status")
    public Result changeStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO statusUpdateDTO) {
        log.info("修改知识条目状态{}", statusUpdateDTO);
         knowledgeService.changeStatus(id, statusUpdateDTO.getStatus());
         return Result.success(null,CodeConstant.SUCCESS,"修改知识条目状态成功");
    }
    @GetMapping("/page")
    public Result<PageResult> page(KnowledgePageQueryDTO knowledgePageQueryDTO)
        {
            log.info("分页查询{}", knowledgePageQueryDTO);
            PageResult pageResult = knowledgeService.page(knowledgePageQueryDTO);
            return Result.success(pageResult,CodeConstant.SUCCESS,"分页查询成功");
    }

}
