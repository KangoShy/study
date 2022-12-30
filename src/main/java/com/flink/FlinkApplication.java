package com.flink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// @ComponentScan({"com.flink.config"})
public class FlinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlinkApplication.class, args);
    }

}
