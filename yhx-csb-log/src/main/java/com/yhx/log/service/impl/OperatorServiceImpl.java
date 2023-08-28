package com.yhx.log.service.impl;

import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.context.CurrentContext;
import com.yhx.log.bean.Operator;
import com.yhx.log.service.OperatorService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author yanghuaxu
 * @date 2023/8/28 16:39
 */
@Service
public class OperatorServiceImpl implements OperatorService {

    @Override
    public Operator getOperator() {
        OperatingUser currentUser = Optional.ofNullable(CurrentContext.getCurrentOperatingUser()).orElse(new OperatingUser());
        return new Operator().setOperatorId(currentUser.getId()).setOperatorName(currentUser.getName());
    }
}
