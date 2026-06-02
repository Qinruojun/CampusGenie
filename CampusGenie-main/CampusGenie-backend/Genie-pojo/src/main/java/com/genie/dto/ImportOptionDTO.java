package com.genie.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 导入选项 DTO
 * 用于接收前端传入的导入参数
 */
@Data
@Builder
public class ImportOptionDTO {
    
    /**
     * 导入策略
     * - ROW: 逐行事务（默认），失败行不影响其他行
     * - ALL: 全部或全不，任一行失败则全部回滚
     */
    private String strategy="ROW";
    
    /**
     * 分类不存在时的处理方式
     * - true: 自动归入"其他未知"分类
     * - false: 校验失败，该行导入失败
     */
    private Boolean autoCategory=true;
}