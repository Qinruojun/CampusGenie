package com.genie.task;


import com.genie.constant.CodeConstant;
import com.genie.result.Result;
import com.genie.service.HotQuestionService;
import com.genie.vo.HotQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class HotQuestionTask {
    @Autowired
    private HotQuestionService hotQuestionService;

    @Scheduled(cron = "0 0 1 * * *")
    public void buildHotQuestion() {
        log.info("定时任务开始执行");
        hotQuestionService.buildHotQuestions();
        log.info("热点更新成功");
    }

}
