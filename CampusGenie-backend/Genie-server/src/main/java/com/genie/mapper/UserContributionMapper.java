package com.genie.mapper;

import com.genie.dto.AdminContributionPageQueryDTO;
import com.genie.dto.ContributionPageQueryDTO;
import com.genie.entity.UserContribution;
import com.genie.vo.AdminContributionVO;
import com.genie.vo.UserContributionVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserContributionMapper {
    void insert(UserContribution userContribution);

    Integer countByUserIdAndQuestion(@Param("userId") Long userId, @Param("question") String question);

    Page<UserContributionVO> pageQueryByUser(@Param("userId") Long userId, @Param("query") ContributionPageQueryDTO contributionPageQueryDTO);

    Page<AdminContributionVO> pageQueryByAdmin(AdminContributionPageQueryDTO adminContributionPageQueryDTO);

    UserContribution selectById(Long id);

    void update(UserContribution userContribution);

    void deleteById(Long id);
}
