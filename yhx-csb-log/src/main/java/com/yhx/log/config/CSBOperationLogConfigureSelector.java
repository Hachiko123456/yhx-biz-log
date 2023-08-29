package com.yhx.log.config;

import com.yhx.log.annotation.EnableCSBOperationLog;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

import javax.annotation.Nullable;

/**
 * @author yanghuaxu
 * @date 2022/6/14 9:16
 */
public class CSBOperationLogConfigureSelector extends AdviceModeImportSelector<EnableCSBOperationLog> {

    private static final String[] ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME =
            new String[]{"com.yhx.log.config.MapperScanConfig", "com.yhx.log.config.OperationLogConfig"};


    @Override
    @Nullable
    public String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return new String[]{AutoProxyRegistrar.class.getName(), OperationLogConfig.class.getName(), MapperScanConfig.class.getName()};
            case ASPECTJ:
                return ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME;
            default:
                return null;
        }
    }
}
