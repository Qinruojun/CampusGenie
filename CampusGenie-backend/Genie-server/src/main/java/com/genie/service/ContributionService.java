package com.genie.service;

import com.genie.dto.ContributionPageQueryDTO;
import com.genie.dto.ContributionSubmitDTO;
import com.genie.result.PageResult;


public interface ContributionService {
    void contribute(ContributionSubmitDTO contributionSubmitDTO);

    PageResult pageQueryByUser(ContributionPageQueryDTO contributionPageQueryDTO);
}
