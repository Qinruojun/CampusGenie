package com.genie.mapper;

import com.genie.entity.UserContribution;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserContributionMapper {
    void insert(UserContribution userContribution);
}
