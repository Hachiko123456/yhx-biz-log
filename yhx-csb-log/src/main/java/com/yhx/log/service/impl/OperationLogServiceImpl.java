package com.yhx.log.service.impl;

import com.cvte.csb.jdbc.mybatis.biz.BaseBiz;
import com.cvte.csb.toolkit.UUIDUtils;
import com.yhx.log.entity.OperationLog;
import com.yhx.log.mapper.OperationLogMapper;
import com.yhx.log.service.OperationLogService;
import com.yhx.log.bean.CommonOperationLog;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 服务实现类
 *
 * @author chenjinbo3303
 * @since 2023-08-08
 */
@Service
@Transactional
public class OperationLogServiceImpl extends BaseBiz<OperationLogMapper, OperationLog> implements OperationLogService {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void record(CommonOperationLog operationLog) {

        OperationLog ol = modelMapper.map(operationLog, OperationLog.class);
        ol.setId(UUIDUtils.get32UUID());
        insertSelective(ol);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void record(List<CommonOperationLog> operationLogList) {
        Optional.ofNullable(operationLogList).ifPresent(os -> os.forEach(o -> {
            OperationLog ol = modelMapper.map(o, OperationLog.class);
            ol.setId(UUIDUtils.get32UUID());
            insertSelective(ol);
        }));
    }

    @Override
    public List<CommonOperationLog> getByBizNo(String bizNo) {
        OperationLog condition = new OperationLog();
        condition.setBizNo(bizNo);
        return modelMapper.map(selectList(condition), new TypeToken<List<CommonOperationLog>>() {
        }.getType());
    }

}
