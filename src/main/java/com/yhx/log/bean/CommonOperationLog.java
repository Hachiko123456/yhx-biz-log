package com.yhx.log.bean;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


/**
 * 通用操作日志
 * @author zhangzihao
 * @since 2022-06-06
 */
@Data
@Builder
public class CommonOperationLog {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Serializable id;

    /**
     * 模块
     */
    private String module;

    /**
     * 业务编号
     */
    private String bizNo;

    /**
     * 链路追踪id
     */
    private String traceId;

    /**
     * 操作
     */
    private String action;

    /**
     * 操作用户id
     */
    private String crtUser;

    /**
     * 操作用户姓名
     */
    private String crtName;

    /**
     * 日志额外信息
     */
    private String detail;

}
