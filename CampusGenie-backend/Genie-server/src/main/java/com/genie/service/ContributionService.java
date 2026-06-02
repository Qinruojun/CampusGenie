package com.genie.service;

import com.genie.dto.AdminContributionPageQueryDTO;
import com.genie.dto.ContributionPageQueryDTO;
import com.genie.dto.ContributionSubmitDTO;
import com.genie.result.PageResult;
import jakarta.validation.Valid;


public interface ContributionService {
    void contribute(ContributionSubmitDTO contributionSubmitDTO);

    PageResult pageQueryByUser(ContributionPageQueryDTO contributionPageQueryDTO);

    PageResult pageQueryByAdmin(@Valid AdminContributionPageQueryDTO adminContributionPageQueryDTO);
}
