package com.flink.config;

import com.flink.constant.DBTypeEnum;
import com.flink.context.MyRoutingDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    /**
     * Master
     */
    @Bean(name = "master")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        // 使用默认连接池HikariPool，这样创建
        return DataSourceBuilder.create().build();
        // 如使用德鲁伊，这样创建
        // return DruidDataSourceBuilder.create().build();
    }


    /**
     * Slave
     */
    @Bean(name = "slave")
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "myRoutingDataSource")
    @Primary
    public MyRoutingDataSource dataSource() {
        MyRoutingDataSource dynamicDataSource = new MyRoutingDataSource();
        //配置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource());
        //配置多数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DBTypeEnum.MASTER.name(), masterDataSource());
        targetDataSources.put(DBTypeEnum.SLAVE.name(), slaveDataSource());
        dynamicDataSource.setTargetDataSources(targetDataSources);
        return dynamicDataSource;
    }

}
