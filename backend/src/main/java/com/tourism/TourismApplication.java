package com.tourism;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TravelVista 旅游景点管理系统 - 启动类
 */
@SpringBootApplication
@MapperScan("com.tourism.mapper")
public class TourismApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourismApplication.class, args);
    }
}
