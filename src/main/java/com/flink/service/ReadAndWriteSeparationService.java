package com.flink.service;

import com.flink.annotation.SelectDataSource;
import com.flink.constant.DBTypeEnum;
import com.flink.context.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReadAndWriteSeparationService {


    @SelectDataSource(value = DBTypeEnum.SLAVE)
    public void select() {

        DBTypeEnum dbTypeEnum = DataSourceContextHolder.get();

        log.info("查询操作，走{}", dbTypeEnum);

    }

    @SelectDataSource(value = DBTypeEnum.MASTER)
    public void update() {

        DBTypeEnum dbTypeEnum = DataSourceContextHolder.get();

        log.info("更新操作，走{}", dbTypeEnum);

    }
}
