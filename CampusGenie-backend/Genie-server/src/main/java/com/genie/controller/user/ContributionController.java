package com.genie.controller.user;


import com.genie.constant.CodeConstant;
import com.genie.dto.ContributionSubmitDTO;
import com.genie.dto.RegisterDTO;
import com.genie.dto.LoginDTO;
import com.genie.result.Result;
import com.genie.service.ContributionService;
import com.genie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class ContributionController {

    @Autowired
    private ContributionService contributionService;

    @PostMapping("/contribute")
    public Result contribute(@RequestBody ContributionSubmitDTO contributionSubmitDTO) {
        contributionService.contribute(contributionSubmitDTO);
        return Result.success(null, CodeConstant.SUCCESS,"贡献成功");
    }

}
