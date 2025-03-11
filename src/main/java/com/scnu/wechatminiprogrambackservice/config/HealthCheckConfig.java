package com.scnu.wechatminiprogrambackservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 健康检查配置类
 */
@Configuration
public class HealthCheckConfig {

    /**
     * MySQL 健康检查
     */
    @Bean
    public HealthIndicator mysqlHealthIndicator(DataSource dataSource) {
        return () -> {
            try {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                // 执行简单查询来检查数据库连接
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                return Health.up()
                        .withDetail("service", "MySQL")
                        .withDetail("status", "Database service is running")
                        .build();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("service", "MySQL")
                        .withDetail("status", "Database service is down")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Redis 健康检查
     */
    @Bean
    public HealthIndicator redisHealthIndicator(@Autowired RedisTemplate<String, Object> redisTemplate) {
        return () -> {
            try {
                // 执行PING命令检查Redis连接
                String result = redisTemplate.getConnectionFactory().getConnection().ping();
                if (result != null) {
                    return Health.up()
                            .withDetail("service", "Redis")
                            .withDetail("status", "Redis service is running")
                            .withDetail("response", result)
                            .build();
                } else {
                    return Health.down()
                            .withDetail("service", "Redis")
                            .withDetail("status", "Redis service is down")
                            .withDetail("response", "No response from Redis")
                            .build();
                }
            } catch (Exception e) {
                return Health.down()
                        .withDetail("service", "Redis")
                        .withDetail("status", "Redis service is down")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }
}
