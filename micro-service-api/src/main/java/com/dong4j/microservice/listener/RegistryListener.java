package com.dong4j.microservice.listener;


import com.dong4j.microservice.framework.registry.ServiceRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author dong4j.
 *     email dong4j@gmail.com
 *     date 2017年10月24日 上午10点:34分
 *     describe 应用启动后自动注册服务
 */
@Component
public class RegistryListener implements ServletContextListener {

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // 获取请求映射
        ServletContext servletContext = servletContextEvent.getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext
            (servletContext);
        RequestMappingHandlerMapping mappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping
            .class);
        Map<RequestMappingInfo, HandlerMethod> infoMap = mappingHandlerMapping.getHandlerMethods();
        // 遍历被 @RequestMapping 修饰的所用方法, 如果此注解的 name 属性不为空, 则使用 name 注册服务
        for (RequestMappingInfo info : infoMap.keySet()) {
            String serviceName = info.getName();
            if (serviceName != null) {
                serviceRegistry.registry(serviceName, String.format("%s:%d", serverAddress, serverPort));
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
