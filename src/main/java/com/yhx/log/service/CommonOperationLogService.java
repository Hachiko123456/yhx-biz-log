package com.yhx.log.service;

import com.yhx.log.bean.CommonOperationLog;

import java.util.List;

/**
 * 服务类
 * @author zhangzihao
 * @since 2022-06-06
 */
public interface CommonOperationLogService {

    void record(CommonOperationLog operationLog);

    void record(List<CommonOperationLog> operationLogList);

}
