package com.paascloud.provider.pmc;

import com.springboot.start.seata.EnableSeata;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.paascloud.provider.pmc","com.paascloud.provider.service.hystrix"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.paascloud.provider.service"})
@MapperScan("com.paascloud.provider.pmc.dao")
@EnableSeata
public class PmcApplication {
    public static void main(String[] args) {
        SpringApplication.run(PmcApplication.class,args);
    }
}
