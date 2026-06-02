package com.genie.service;

import com.genie.dto.ContributionSubmitDTO;
import com.genie.vo.UserContributionVO;

public interface ContributionService {
    UserContributionVO contribute(ContributionSubmitDTO contributionSubmitDTO);
}
