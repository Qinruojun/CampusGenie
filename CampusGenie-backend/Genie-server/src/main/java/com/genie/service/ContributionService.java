package com.genie.service;

import com.genie.dto.*;
import com.genie.result.PageResult;
import com.genie.vo.BatchReviewVO;
import jakarta.validation.Valid;


public interface ContributionService {
    void contribute(ContributionSubmitDTO contributionSubmitDTO);

    PageResult pageQueryByUser(ContributionPageQueryDTO contributionPageQueryDTO);

    PageResult pageQueryByAdmin(@Valid AdminContributionPageQueryDTO adminContributionPageQueryDTO);

    void approve(Long id, @Valid ApproveDTO approveDTO);

    void reject(Long id, @Valid RejectDTO rejectDTO);

    void delete(Long id);

    BatchReviewVO batchReview(@Valid BatchReviewDTO batchReviewDTO);
}
