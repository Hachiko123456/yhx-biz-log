package com.yhx.log.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yanghuaxu
 * @date 2022/6/20 14:58
 */
@Data
@Accessors(chain = true)
public class Operator {

    private String operatorId;

    private String operatorName;

}
