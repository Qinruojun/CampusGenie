package com.genie.controller.admin;

import com.genie.dto.KnowledgeDTO;
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
         knowledgeService.newKnowledge(knowledgeDTO);
         return Result.success(null,200,"新增知识条目成功");
    }
//    @PutMapping("/edit")//修改知识条目
//    public Result editKnowledge(@Valid @RequestBody KnowledgeDTO knowledgeDTO) {
//         knowledgeService.editKnowledge(knowledgeDTO);
//         return null;
//    }
//    @DeleteMapping("/{id}/delete")//删除知识条目
//    public Result deleteKnowledge(@PathVariable Long id) {//将URL中的id取出来传给方法里的id
//         knowledgeService.deleteKnowledge(id);
//         return null;
//    }

}
