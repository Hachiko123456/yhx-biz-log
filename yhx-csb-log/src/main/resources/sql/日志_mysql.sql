CREATE TABLE common_operation_log(
	`id` VARCHAR(36) COMMENT'主键',
	`module` VARCHAR(100) DEFAULT NULL COMMENT '模块',
	`biz_no` VARCHAR(100) DEFAULT NULL COMMENT '业务编号',
	`trace_id` VARCHAR(36) DEFAULT NULL COMMENT '链路追踪id',
	`action` TEXT DEFAULT NULL COMMENT '操作',
	`crt_user` VARCHAR(100) DEFAULT NULL COMMENT '操作用户id',
	`crt_name` VARCHAR(100) DEFAULT NULL COMMENT '操作用户姓名',
	`detail` TEXT DEFAULT NULL COMMENT '修改的详细信息',
	`crt_time` TIMESTAMP DEFAULT NULL COMMENT '创建时间',
	PRIMARY KEY (ID)
)ENGINE=InnoDB COMMENT='通用操作日志';
alter table common_operation_log add index idx_biz_no(biz_no);
alter table common_operation_log add index idx_trace_id(trace_id);
