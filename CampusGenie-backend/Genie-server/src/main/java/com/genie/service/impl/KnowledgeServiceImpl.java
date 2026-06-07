package com.genie.service.impl;

import com.genie.constant.ActionTypeConstant;
import com.genie.constant.TargetTypeConstant;
import com.genie.context.BaseContext;
import com.genie.dto.KnowledgeDTO;
import com.genie.dto.KnowledgePageQueryDTO;
import com.genie.entity.AdminLog;
import com.genie.entity.Category;
import com.genie.entity.KnowledgeBase;
import com.genie.exception.KnowledgeBaseStatusException;
import com.genie.mapper.AdminLogMapper;
import com.genie.mapper.CategoryMapper;
import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.result.PageResult;
import com.genie.service.KnowledgeService;
import com.genie.vo.BatchDeleteVO;
import com.genie.vo.KnowledgeExportVO;
import com.genie.vo.KnowledgeVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;
    @Autowired
    private AdminLogMapper adminLogMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void newKnowledge(KnowledgeDTO knowledgeDTO) {
        //TODO 缺少向量检索看是否有相似问题回答待解决
        //插入知识条目
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        BeanUtils.copyProperties(knowledgeDTO,knowledgeBase);
        knowledgeBase.setCreatedBy(BaseContext.getCurrentUsername());
        knowledgeBase.setUpdatedBy(BaseContext.getCurrentUsername());
        knowledgeBase.setCreatedTime(LocalDateTime.now());
        knowledgeBase.setUpdatedTime(LocalDateTime.now());
        knowledgeBaseMapper.insert(knowledgeBase);
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.INSERT)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .targetId(knowledgeBase.getId())
                .createdTime(LocalDateTime.now())
                .build();
        adminLogMapper.insert(adminLog);
    }

    @Override
    @Transactional
    public void editKnowledge(KnowledgeDTO knowledgeDTO) {
        //获取旧知识条目
        KnowledgeBase oldKnowledgeBase = knowledgeBaseMapper.selectById(knowledgeDTO.getId());
        //编写管理员操作前后知识条目细节（json格式）{

        String before = "{\"question\":\"" + oldKnowledgeBase.getQuestion()
                + "\",\"answer\":\"" + oldKnowledgeBase.getAnswer()
                + "\",\"categoryId\":" + oldKnowledgeBase.getCategoryId()
                + ",\"source\":\"" + oldKnowledgeBase.getSource()
                + "\",\"status\":" + oldKnowledgeBase
                .getStatus() + "}";
        String after = "{\"question\":\"" + knowledgeDTO.getQuestion()
                + "\",\"answer\":\"" + knowledgeDTO.getAnswer()
                + "\",\"categoryId\":" + knowledgeDTO.getCategoryId()
                + ",\"source\":\"" + knowledgeDTO.getSource()
                + "\",\"status\":" + knowledgeDTO
                .getStatus() + "}";
        String details = "{\"before\":" + before + ",\"after\":" + after + "}";


        //编辑知识条目并调用mapper更新
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        BeanUtils.copyProperties(knowledgeDTO,knowledgeBase);
        knowledgeBase.setUpdatedBy(BaseContext.getCurrentUsername());
        knowledgeBase.setUpdatedTime(LocalDateTime.now());
        knowledgeBaseMapper.update(knowledgeBase);
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.UPDATE)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .targetId(knowledgeBase.getId())
                .detail(details)
                .createdTime(LocalDateTime.now())
                .build();
        adminLogMapper.insert(adminLog);
    }

    @Override
    @Transactional
    public void deleteKnowledge(Long id) {
        //删除知识条目并调用mapper更新
        knowledgeBaseMapper.deleteById(id);
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.DELETE)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .targetId(id)
                .createdTime(LocalDateTime.now())
                .build();
                adminLogMapper.insert(adminLog);
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        //获取要修改的知识条目的状态并判断是否与知识库存储状态一致，不一致报错，一致修改
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectByIdAndStatus(id,status);
        if(knowledgeBase== null){
            throw new KnowledgeBaseStatusException("知识条目状态异常");
        }
        //修改状态
        knowledgeBase.setStatus(1-status);
        knowledgeBaseMapper.update(knowledgeBase);
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.UPDATE)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .targetId(knowledgeBase.getId())
                .detail("{\"before\":{\"status\":" + status + "},\"after\":{\"status\":" + (1-status) + "}}")
                .createdTime(LocalDateTime.now())
                .build();
        adminLogMapper.insert(adminLog);


    }

    @Override
    public PageResult page(@Valid  KnowledgePageQueryDTO knowledgePageQueryDTO) {
        PageHelper.startPage(knowledgePageQueryDTO.getPage(),knowledgePageQueryDTO.getPageSize());
        Page<KnowledgeVO> page = knowledgeBaseMapper.pageQuery(knowledgePageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    @Transactional
    public BatchDeleteVO batchDelete(@RequestParam  List<Long> ids) {
        //采取逐行删除的方式
        BatchDeleteVO batchDeleteVO = new BatchDeleteVO();
        int successCount = 0;
        int failCount = 0;
        List<Long> failIds = new ArrayList<>();
        for(Long id : ids){
            //先判断是否存在，若存在判断状态是否为0，若为0则删除，若不为0则失败
            KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectByIdAndStatus(id,0);
            if(knowledgeBase != null){
                knowledgeBaseMapper.deleteById(id);
                successCount++;

            }
            else{
                failCount++;
                failIds.add(id);
            }
        }
        batchDeleteVO.setSuccessCount(successCount);
        batchDeleteVO.setFailCount(failCount);
        batchDeleteVO.setFailIds(failIds);
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.BATCH_DELETE)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .detail("{\"successCount\":" + successCount + ",\"failCount\":" + failCount + ",\"failIds\":" + failIds + "}")
                .createdTime(LocalDateTime.now())
                .build();
                adminLogMapper.insert(adminLog);
        return batchDeleteVO;


    }
    @Transactional
    public void exportToExcel(String keyword, Integer categoryId, Integer status,
                              HttpServletResponse response) throws IOException {

        // 1. 查询数据
        List<KnowledgeExportVO> list = getExportList(keyword, categoryId, status);

        // 2. 创建 Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("知识库");

        // 3. 创建表头
        String[] headers = {"问题", "回答", "分类", "来源"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 4. 填充数据
        int rowNum = 1;
        for (KnowledgeExportVO vo : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(vo.getQuestion() != null ? vo.getQuestion() : "");
            row.createCell(1).setCellValue(vo.getAnswer() != null ? vo.getAnswer() : "");
            row.createCell(2).setCellValue(vo.getCategoryName() != null ? vo.getCategoryName() : "");
            row.createCell(3).setCellValue(vo.getSource() != null ? vo.getSource() : "");
        }

        // 5. 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 6. 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=knowledge_" + LocalDate.now() + ".xlsx");

        // 7. 写入响应流
        workbook.write(response.getOutputStream());
        workbook.close();
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.EXPORT)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .detail("{\"keyword\":\"" + keyword + "\",\"categoryId\":" + categoryId + ",\"status\":" + status + "}")
                .createdTime(LocalDateTime.now())
                .build();
                adminLogMapper.insert(adminLog);
    }

    @Override
    public void downloadExcelTemplate(HttpServletResponse response) throws IOException {
        // 模板文件路径（根据实际文件名修改）
        String templatePath = "template/KnowledgeBaseImportTemplate.xlsx";

        // 读取模板文件
        ClassPathResource resource = new ClassPathResource(templatePath);

        if (!resource.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":404,\"msg\":\"模板文件不存在\"}");
            return;
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=knowledge_template.xlsx");

        // 复制文件到输出流
        try (InputStream inputStream = resource.getInputStream();
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
    }

    @Override
    public void downloadJsonTemplate(HttpServletResponse response) throws IOException {
        // 模板文件路径（根据实际文件名修改）
        String templatePath = "template/KnowledgeBaseImportTemplate.json";

        // 读取模板文件
        ClassPathResource resource = new ClassPathResource(templatePath);

        if (!resource.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":404,\"msg\":\"模板文件不存在\"}");
            return;
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=knowledge_template.json");

        // 复制文件到输出流
        try (InputStream inputStream = resource.getInputStream();
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
    }

    private List<KnowledgeExportVO> getExportList(String keyword, Integer categoryId, Integer status) {
        // 查询知识库
        List<KnowledgeBase> list = knowledgeBaseMapper.selectByKeyword_CategoryId_Status( keyword, categoryId, status);

        // 转换为导出 VO
        return list.stream().map(kb -> {
            KnowledgeExportVO vo = new KnowledgeExportVO();
            vo.setQuestion(kb.getQuestion());
            vo.setAnswer(kb.getAnswer());
            vo.setSource(kb.getSource());

            // 获取分类名称
            if (kb.getCategoryId() != null) {
                Category category = categoryMapper.selectById(kb.getCategoryId());
                vo.setCategoryName(category != null ? category.getName() : "");
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
