package com.genie.controller.admin;

import com.genie.constant.CodeConstant;
import com.genie.constant.MessageConstant;
import com.genie.result.Result;
import com.genie.service.HotQuestionService;
import com.genie.vo.HotQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminHotQuestionController {//管理员刷新热门问题
    @Autowired
    HotQuestionService hotQuestionService;
    @RequestMapping( "/hotquestions")
    public Result flushHotQuestions(){
        List<HotQuestionVO> list=hotQuestionService.getHotQuestions();
        if (list != null) {
            return Result.success(list, CodeConstant.SUCCESS, MessageConstant.OPERATION_SUCCESS);
        }
        else return Result.error(CodeConstant.INTERNAL_SERVER_ERROR, MessageConstant.OPERATION_FAILED);//显示因为系统内部错误，操作失败
    }


}
