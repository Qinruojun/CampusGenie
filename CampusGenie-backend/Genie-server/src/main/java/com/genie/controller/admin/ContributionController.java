package com.genie.controller.admin;

import com.genie.constant.CodeConstant;
import com.genie.dto.AdminContributionPageQueryDTO;
import com.genie.dto.ContributionPageQueryDTO;
import com.genie.dto.ContributionSubmitDTO;
import com.genie.dto.KnowledgeDTO;
import com.genie.result.PageResult;
import com.genie.result.Result;
import com.genie.service.ContributionService;
import com.genie.vo.UserContributionVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("adminContributionController")
@RequestMapping("/admin/contributions")
public class ContributionController  {
    //管理员审核贡献
    @Autowired
    private ContributionService contributionService;


    @GetMapping("/page")
    public Result pageQuery(@Valid AdminContributionPageQueryDTO adminContributionPageQueryDTO) {
        log.info("分页查询用户贡献信息：{}", adminContributionPageQueryDTO);
        PageResult pageResult = contributionService.pageQueryByAdmin(adminContributionPageQueryDTO);
        return Result.success(pageResult, CodeConstant.SUCCESS, "分页查询成功");
    }


}
