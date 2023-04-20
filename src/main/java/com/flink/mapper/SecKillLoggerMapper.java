package com.flink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flink.model.SecKillLogger;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SecKillLoggerMapper extends BaseMapper<SecKillLogger> {
}
