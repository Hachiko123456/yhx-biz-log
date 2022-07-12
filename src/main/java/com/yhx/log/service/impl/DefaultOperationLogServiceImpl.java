package com.yhx.log.service.impl;

import com.google.common.collect.Lists;
import com.yhx.log.bean.CommonOperationLog;
import com.yhx.log.service.CommonOperationLogService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * 默认日志实现类
 * @author yanghuaxu
 * @since 2022-06-06
 */
@Slf4j
public class DefaultOperationLogServiceImpl implements CommonOperationLogService {


    @Override
    public void record(CommonOperationLog operationLog) {
        log.info("log record: {}", operationLog);
    }

    @Override
    public void record(List<CommonOperationLog> operationLogList) {
        Optional.ofNullable(operationLogList).ifPresent(o -> log.info("log record: {}", o));
    }

    @Override
    public List<CommonOperationLog> getByBizNo(String bizNo) {
        return Lists.newArrayList();
    }
}
