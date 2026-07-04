package com.genie.controller.admin;

import com.genie.entity.AdminLog;
import com.genie.result.Result;
import com.genie.service.AdminLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/logs")
public class AdminLogController {

    private final AdminLogService adminLogService;

    public AdminLogController(AdminLogService adminLogService) {
        this.adminLogService = adminLogService;
    }

    @GetMapping("/recent")
    public Result<List<AdminLog>> getRecentLogs(@RequestParam(defaultValue = "10") Integer limit) {
        List<AdminLog> logs = adminLogService.getRecentLogs(limit);
        return Result.success(logs);
    }
}