package com.dong4j.microservice.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author dong4j.
 *     email dong4j@gmail.com
 *     date 2017年10月24日 上午10点:34分
 *     describe
 */
@RestController
public class HelloController {
    @RequestMapping(path = "/hello", method = RequestMethod.GET, name = "HelloService")
    public String hello() {
        return "Hello Spring Boot!";
    }
}
