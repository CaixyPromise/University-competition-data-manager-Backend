package com.caixy.notificationservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.caixy")
@MapperScan("com.caixy.notificationservice.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableFeignClients(basePackages = {"com.caixy.serviceclient.service"})
public class NotificationServiceApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

}
