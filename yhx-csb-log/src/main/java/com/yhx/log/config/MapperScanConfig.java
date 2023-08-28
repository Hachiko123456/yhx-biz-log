package com.yhx.log.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanghuaxu
 * @date 2023/8/28 17:29
 */
@Configuration
@MapperScan("com.yhx.log.mapper")
@ComponentScan(basePackages = {"com.yhx.log"})
public class MapperScanConfig {
}
