package com.caixy.teamservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.caixy.teamservice.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.caixy")
@EnableFeignClients(basePackages = {"com.caixy.serviceclient.service"})
public class TeamServiceApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(TeamServiceApplication.class, args);
    }

}
