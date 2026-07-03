package com.genie.controller.admin;

import com.genie.constant.CodeConstant;
import com.genie.dto.*;
import com.genie.result.PageResult;
import com.genie.result.Result;
import com.genie.service.ContributionService;
import com.genie.vo.BatchReviewVO;
import com.genie.vo.ContributionStatisticsVO;
import com.genie.vo.UserContributionVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminContributionController")
@RequestMapping("/admin/contributions")
public class ContributionController  {
    //管理员审核贡献
    @Autowired
    private ContributionService contributionService;


    @GetMapping("/page")
    public Result pageQuery(@Valid AdminContributionPageQueryDTO adminContributionPageQueryDTO) {
        log.info("分页查询用户贡献信息：{}", adminContributionPageQueryDTO);
        PageResult pageResult = contributionService.pageQueryByAdmin(adminContributionPageQueryDTO);
        return Result.success(pageResult, CodeConstant.SUCCESS, "分页查询成功");
    }
    @PutMapping("/{id}/approve")
    public Result approve(@PathVariable Long id, @Valid @RequestBody ApproveDTO approveDTO) {
        log.info("审核通过用户贡献信息：{}", id);
        contributionService.approve(id, approveDTO);
        return Result.success(null, CodeConstant.SUCCESS, "审核通过成功");
    }
    @PutMapping("/{id}/reject")
    public Result reject(@PathVariable Long id, @Valid @RequestBody RejectDTO rejectDTO) {
        log.info("审核拒绝用户贡献信息：{}", id);
        contributionService.reject(id, rejectDTO);
        return Result.success(null, CodeConstant.SUCCESS, "审核拒绝成功");
    }
    @PostMapping("/review/batch")
    public Result batchReview(@Valid @RequestBody BatchReviewDTO batchReviewDTO) {
        log.info("批量审核用户贡献信息：{}", batchReviewDTO);
        BatchReviewVO result = contributionService.batchReview(batchReviewDTO);
        if (result.getFailCount() == 0) {
            return Result.success(result, "批量审核完成");
        } else {
            return Result.success(result, String.format("批量审核完成，成功%d条，失败%d条",
                    result.getSuccessCount(), result.getFailCount()));
        }

    }

    @GetMapping("/statistics")
    public Result getStatistics() {
        log.info("获取贡献统计数据");
        ContributionStatisticsVO statistics = contributionService.getStatistics();
        return Result.success(statistics, CodeConstant.SUCCESS, "获取统计数据成功");
    }

    @GetMapping("/pending-count")
    public Result getPendingCount() {
        log.info("获取待审核数量");
        Integer count = contributionService.getPendingReviewCount();
        return Result.success(count, CodeConstant.SUCCESS, "获取待审核数量成功");
    }

}
