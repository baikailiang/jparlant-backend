package com.jparlant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jparlant.mapper")
public class JparlantBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JparlantBackendApplication.class, args);
        System.out.println("JParlant Backend Server Started!");
    }
}
