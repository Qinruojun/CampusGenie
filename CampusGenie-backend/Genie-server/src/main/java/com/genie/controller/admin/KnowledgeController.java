package com.genie.controller.admin;

import com.genie.constant.CodeConstant;
import com.genie.dto.ImportOptionDTO;
import com.genie.dto.KnowledgeDTO;
import com.genie.dto.KnowledgePageQueryDTO;
import com.genie.dto.StatusUpdateDTO;
import com.genie.result.PageResult;
import com.genie.result.Result;
import com.genie.service.KnowledgeImportService;
import com.genie.service.KnowledgeService;
import com.genie.vo.ImportResultVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/admin/knowledge")
public class KnowledgeController {
    @Autowired
    private KnowledgeService knowledgeService;
    @Autowired
    private KnowledgeImportService knowledgeImportService;
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
    @PostMapping(value = "/import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ImportResultVO>importKnowledge(@RequestParam("file") MultipartFile file,@ModelAttribute ImportOptionDTO option){
        log.info("批量导入知识条目 - 文件名: {}, 大小: {}KB, 策略: {}, 自动分类: {}",
                file.getOriginalFilename(),
                file.getSize() / 1024,
                option.getStrategy(),
                option.getAutoCategory());
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        // 2. 文件格式校验
        String fileName = file.getOriginalFilename();
        if (fileName == null || !isSupportedFileType(fileName)) {
            return Result.error(CodeConstant.BAD_REQUEST,"文件格式不支持，仅支持 .xlsx、.xls、.json 格式");
        }

        // 3. 文件大小校验（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error(CodeConstant.BAD_REQUEST,"文件大小不能超过 10MB");
        }
        ImportResultVO result = knowledgeImportService.importKnowledge(file, option);

        return Result.success(result, CodeConstant.SUCCESS, "导入完成");
    }
    /**
     * 判断文件类型是否支持
     */
    private boolean isSupportedFileType(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".xlsx") ||
                lowerName.endsWith(".xls") ||
                lowerName.endsWith(".json");
    }

}
