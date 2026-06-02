package com.genie.mapper;

import com.genie.dto.ContributionPageQueryDTO;
import com.genie.entity.UserContribution;
import com.genie.vo.UserContributionVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserContributionMapper {
    void insert(UserContribution userContribution);

    Page<UserContributionVO> pageQueryByUser(@Param("userId") Long userId, @Param("query") ContributionPageQueryDTO contributionPageQueryDTO);
}
