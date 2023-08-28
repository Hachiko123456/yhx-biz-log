CREATE TABLE COMMON_OPERATION_LOG(
	ID VARCHAR2(36) ,
	MODULE VARCHAR2(100) DEFAULT NULL,
	BIZ_NO VARCHAR2(100) DEFAULT NULL,
	TRACE_ID VARCHAR2(36) DEFAULT NULL,
	ACTION CLOB DEFAULT NULL,
	CRT_USER VARCHAR2(100) DEFAULT NULL,
	CRT_NAME VARCHAR2(100) DEFAULT NULL,
	DETAIL CLOB DEFAULT NULL,
	CRT_TIME TIMESTAMP DEFAULT NULL,
	CONSTRAINT PK_COMMON_OPERATION_LOG PRIMARY KEY (ID)
);
COMMENT ON TABLE COMMON_OPERATION_LOG IS '通用操作日志';
COMMENT ON COLUMN COMMON_OPERATION_LOG.ID IS '主键';
COMMENT ON COLUMN COMMON_OPERATION_LOG.module IS '模块';
COMMENT ON COLUMN COMMON_OPERATION_LOG.biz_no IS '业务编号';
COMMENT ON COLUMN COMMON_OPERATION_LOG.trace_id IS '链路追踪id';
COMMENT ON COLUMN COMMON_OPERATION_LOG.action IS '操作';
COMMENT ON COLUMN COMMON_OPERATION_LOG.crt_user IS '操作用户id';
COMMENT ON COLUMN COMMON_OPERATION_LOG.crt_name IS '操作用户姓名';
COMMENT ON COLUMN COMMON_OPERATION_LOG.detail IS '修改的详细信息';
COMMENT ON COLUMN COMMON_OPERATION_LOG.crt_time IS '创建时间';
CREATE INDEX IDX_COMMON_OPERATION_LOG_biz_no ON COMMON_OPERATION_LOG(biz_no);
CREATE INDEX IDX_COMMON_OPERATION_LOG_trace_id ON COMMON_OPERATION_LOG(trace_id);
