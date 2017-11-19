package com.dong4j.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author dong4j.
 *     email dong4j@gmail.com
 *     date 2017年10月24日 上午10点:34分
 *     describe  spring boot 启动类
 */
@SpringBootApplication(scanBasePackages = "com.dong4j")
public class MicroServiceApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceApiApplication.class, args);
    }

}
