package com.spr.sec.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.spr.sec.**"
})
@EntityScan(basePackages = {
        "com.spr.sec.comp.domain",
        "com.spr.sec.comp.service",
})
@EnableJpaRepositories(basePackages = {
        "com.spr.sec.comp.repository"
})
public class ServerCentralApplication {

    public static void main(String args[]){
        SpringApplication.run(ServerCentralApplication.class,args);
    }
}
