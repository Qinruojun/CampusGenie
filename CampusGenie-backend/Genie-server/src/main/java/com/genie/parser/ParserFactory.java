package com.genie.parser;

import com.genie.exception.FileFormatNotSupportedException;
import com.genie.exception.FileNameInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@Component
public class ParserFactory {

    @Autowired
    private List<KnowledgeParser> parsers;  // Spring 会自动注入所有 KnowledgeParser 实现类

    /**
     * 根据文件获取对应的解析器
     */
    public KnowledgeParser getParser(MultipartFile file) {
        // 1. 获取文件扩展名
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            throw new FileNameInvalidException("文件名格式错误");
        }
        
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        log.info("文件类型: {}", fileType);
        
        // 2. 遍历所有解析器，找到支持的
        for (KnowledgeParser parser : parsers) {
            if (parser.supports(fileType)) {
                log.info("找到解析器: {}", parser.getClass().getSimpleName());
                return parser;
            }
        }
        
        // 3. 兜底：理论上不会走到这里，因为 Controller 已经校验过
        //    但为了代码健壮性，还是抛出一个明确的异常
        throw  new FileFormatNotSupportedException( "不支持的文件格式");
    }
}