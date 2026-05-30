package com.genie.controller.user;

import com.genie.constant.CodeConstant;
import com.genie.result.Result;
import com.genie.service.HotQuestionService;
import com.genie.vo.HotQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class HotQuestionController {
    @Autowired
    private HotQuestionService hotQuestionService;
    @PostMapping( "/hotquestions")
    public Result  getHotQuestions(){
        List<HotQuestionVO> list=hotQuestionService.getHotQuestions();
        return Result.success(list, CodeConstant.SUCCESS,"查询成功");
    }

}
