package com.genie.service.impl;

import com.genie.entity.AdminLog;
import com.genie.mapper.AdminLogMapper;
import com.genie.service.AdminLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminLogServiceImpl implements AdminLogService {

    private final AdminLogMapper adminLogMapper;

    public AdminLogServiceImpl(AdminLogMapper adminLogMapper) {
        this.adminLogMapper = adminLogMapper;
    }

    @Override
    public List<AdminLog> getRecentLogs(Integer limit) {
        return adminLogMapper.selectRecent(0, limit);
    }
}
