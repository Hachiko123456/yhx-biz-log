package com.yhx.log.service;


import com.yhx.log.bean.Operator;

/**
 * @author yanghuaxu
 * @date 2022/6/20 14:57
 */
public interface OperatorService {

    /**
     * 获取当前操作用户
     * @param
     * @return com.cvte.his.dto.Operator
     **/
    Operator getOperator();

}
