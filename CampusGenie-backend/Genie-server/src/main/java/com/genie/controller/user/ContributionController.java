package com.genie.controller.user;


import com.genie.constant.CodeConstant;
import com.genie.context.BaseContext;
import com.genie.dto.ContributionPageQueryDTO;
import com.genie.dto.ContributionSubmitDTO;
import com.genie.dto.RegisterDTO;
import com.genie.dto.LoginDTO;
import com.genie.result.PageResult;
import com.genie.result.Result;
import com.genie.service.ContributionService;
import com.genie.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userContributionController")
@RequestMapping("/user")
public class ContributionController {

    @Autowired
    private ContributionService contributionService;

    @PostMapping("/contribute")
    public Result contribute(@Valid @RequestBody ContributionSubmitDTO contributionSubmitDTO) {
        contributionService.contribute(contributionSubmitDTO);
        return Result.success(null, CodeConstant.SUCCESS,"贡献成功");
    }
    @GetMapping("/contributions/page")
    public Result<PageResult> page(@Valid ContributionPageQueryDTO contributionPageQueryDTO)
        {
            log.info("分页查询{}", contributionPageQueryDTO);

            PageResult pageResult = contributionService.pageQueryByUser(contributionPageQueryDTO);
            return Result.success(pageResult,CodeConstant.SUCCESS,"分页查询成功");
    }

}
