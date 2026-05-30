package com.genie.controller.admin;

import com.genie.constant.CodeConstant;
import com.genie.constant.MessageConstant;
import com.genie.result.Result;
import com.genie.service.CategoryService;
import com.genie.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @GetMapping("/categories")
    public Result getCategoryList() {
      List<CategoryVO> list = categoryService.getCategoryList();
      if(list!=null){
          return Result.success(list, CodeConstant.SUCCESS, MessageConstant.OPERATION_SUCCESS);
      }
      return Result.error(CodeConstant.NOT_FOUND, MessageConstant.NOT_FOUND);
    }
}
