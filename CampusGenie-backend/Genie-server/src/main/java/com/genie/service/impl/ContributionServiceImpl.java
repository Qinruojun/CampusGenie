package com.genie.service.impl;

import com.genie.constant.StatusConstant;
import com.genie.context.BaseContext;
import com.genie.dto.ContributionSubmitDTO;
import com.genie.entity.UserContribution;
import com.genie.mapper.UserContributionMapper;
import com.genie.service.ContributionService;
import com.genie.vo.UserContributionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ContributionServiceImpl implements ContributionService {
    @Autowired
    private UserContributionMapper userContributionMapper;
    @Override
    public void contribute(ContributionSubmitDTO contributionSubmitDTO){
        //TODO 是否还需校验（敏感词？非空上层校验了）
        //创建实体并插入数据库
        UserContribution userContribution = new UserContribution();
        BeanUtils.copyProperties(contributionSubmitDTO,userContribution);
        userContribution.setUserId(BaseContext.getCurrentUserId());
        userContribution.setStatus(StatusConstant.WAIT_FOR_REVIEW);
        userContribution.setCreatedTime(LocalDateTime.now());
        userContributionMapper.insert(userContribution);
    }
}
