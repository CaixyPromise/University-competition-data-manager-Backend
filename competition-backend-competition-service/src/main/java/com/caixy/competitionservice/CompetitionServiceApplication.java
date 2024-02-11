package com.caixy.competitionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.caixy.competitionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.caixy")
@EnableDiscoveryClient
//@EnableFeignClients(basePackages = {"com.caixy.serviceclient.service"})
public class CompetitionServiceApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(CompetitionServiceApplication.class, args);
    }
}
