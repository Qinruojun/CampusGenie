package com.genie.controller.admin;

import com.genie.service.ContributionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminContributionController {
    //管理员审核贡献
    @Autowired
    private ContributionService contributionService;


}
