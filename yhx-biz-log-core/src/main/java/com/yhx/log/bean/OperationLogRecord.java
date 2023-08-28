package com.yhx.log.bean;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yanghuaxu
 * @date 2022/6/6 20:22
 */
@Data
@Accessors(chain = true)
@Builder
public class OperationLogRecord {

    private String module;

    private String bizNo;

    private String traceId;

    private String operator;

    private String detail;

    private String[] detailArgs;

    private String success;

    private String fail;

    private String condition;

    private boolean isBatch;

}
