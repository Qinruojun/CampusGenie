package com.genie.service.impl;

import com.genie.constant.StatusConstant;
import com.genie.context.BaseContext;
import com.genie.dto.AdminContributionPageQueryDTO;
import com.genie.dto.ContributionPageQueryDTO;
import com.genie.dto.ContributionSubmitDTO;
import com.genie.entity.UserContribution;
import com.genie.mapper.UserContributionMapper;
import com.genie.result.PageResult;
import com.genie.service.ContributionService;
import com.genie.vo.AdminContributionVO;
import com.genie.vo.UserContributionVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public PageResult pageQueryByUser(ContributionPageQueryDTO contributionPageQueryDTO) {
        PageHelper.startPage(contributionPageQueryDTO.getPage(),contributionPageQueryDTO.getPageSize());
        Long userId = BaseContext.getCurrentUserId();
        Page<UserContributionVO> page = userContributionMapper.pageQueryByUser(userId,contributionPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    public PageResult pageQueryByAdmin(AdminContributionPageQueryDTO adminContributionPageQueryDTO) {
        PageHelper.startPage(adminContributionPageQueryDTO.getPage(),adminContributionPageQueryDTO.getPageSize());
        Page<AdminContributionVO> page = userContributionMapper.pageQueryByAdmin(adminContributionPageQueryDTO);
        //对用户名字脱敏 如张三->张**
        for (AdminContributionVO adminContributionVO : page) {
            String username = adminContributionVO.getUsername();
            adminContributionVO.setUsername(username.substring(0,1)+"**");

        }
        return new PageResult(page.getTotal(),page.getResult());

    }
}
