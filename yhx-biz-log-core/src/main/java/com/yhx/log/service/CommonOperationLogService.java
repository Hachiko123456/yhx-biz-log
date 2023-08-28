package com.yhx.log.service;

import com.yhx.log.bean.CommonOperationLog;

import java.util.List;

/**
 * 服务类
 * @author zhangzihao
 * @since 2022-06-06
 */
public interface CommonOperationLogService {

    /**
     * 记录日志
     * @param operationLog
     * @return void
     **/
    void record(CommonOperationLog operationLog);

    /**
     * 批量记录日志
     * @param operationLogList
     * @return void
     **/
    void record(List<CommonOperationLog> operationLogList);

    /**
     * 根据bizNo查询日志信息
     * @param bizNo
     * @return java.util.List<com.yhx.log.bean.CommonOperationLog>
     **/
    List<CommonOperationLog> getByBizNo(String bizNo);

}
