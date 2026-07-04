package com.genie.service;

import com.genie.entity.AdminLog;

import java.util.List;

public interface AdminLogService {
    List<AdminLog> getRecentLogs(Integer limit);
}
