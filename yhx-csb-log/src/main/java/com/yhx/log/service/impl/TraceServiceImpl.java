package com.yhx.log.service.impl;

import com.cvte.csb.web.log.entity.BaseLog;
import com.yhx.log.service.TraceService;
import org.springframework.stereotype.Service;

/**
 * @author yanghuaxu
 * @date 2022/6/20 18:31
 */
@Service
public class TraceServiceImpl implements TraceService {
    @Override
    public String getTraceId() {
        return BaseLog.getRequestTraceId();
    }
}
