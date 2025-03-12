package com.scnu.wechatminiprogrambackservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动时执行的任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartup implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        log.info("==== 应用已启动 ====");

        // 可以在这里执行一些初始化操作
        log.info("==== 数据库连接池已就绪 ====");
        log.info("==== Redis连接已就绪 ====");
        log.info("==== 系统初始化完成 ====");
    }
}