package com.cvte.yhx.log.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 通用操作日志
 *
 * @author chenjinbo3303
 * @since 2023-08-08
 */
@Table(name = "common_operation_log")
@ApiModel(description = "通用操作日志")
@Data
@EqualsAndHashCode
public class OperationLog {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 模块
     */
    @ApiModelProperty(value = "模块")
    private String module;
    /**
     * 业务编号
     */
    @Column(name = "biz_no")
    @ApiModelProperty(value = "业务编号")
    private String bizNo;
    /**
     * 链路追踪id
     */
    @Column(name = "trace_id")
    @ApiModelProperty(value = "链路追踪id")
    private String traceId;
    /**
     * 操作
     */
    @ApiModelProperty(value = "操作")
    private String action;
    /**
     * 操作用户id
     */
    @Column(name = "crt_user")
    @ApiModelProperty(value = "操作用户id")
    private String crtUser;
    /**
     * 操作用户姓名
     */
    @Column(name = "crt_name")
    @ApiModelProperty(value = "操作用户姓名")
    private String crtName;
    /**
     * 修改的详细信息
     */
    @ApiModelProperty(value = "修改的详细信息")
    private String detail;
    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

}
