package com.genie.controller.admin;

import com.genie.dto.ContributionSubmitDTO;
import com.genie.dto.KnowledgeDTO;
import com.genie.service.ContributionService;
import com.genie.vo.UserContributionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("adminContributionController")
@RequestMapping("/admin")
public class ContributionController  {
    //管理员审核贡献
    @Autowired
    private ContributionService contributionService;


}
