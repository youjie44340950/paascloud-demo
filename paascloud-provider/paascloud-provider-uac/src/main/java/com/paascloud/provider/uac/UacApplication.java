package com.paascloud.provider.uac;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.paascloud.provider.uac.dao")
public class UacApplication {
    public static void main(String[] args) {
        SpringApplication.run(UacApplication.class,args);
    }
}
