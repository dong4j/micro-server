package com.dong4j.microservice.registry;


import com.dong4j.microservice.framework.registry.ServiceRegistry;

import org.springframework.stereotype.Component;

/**
 * @author dong4j.
 * email dong4j@gmail.com
 * date 2017年10月24日 上午10点:34分
 * describe 服务注册实现
 */
@Component
public class ServiceRegistryImpl implements ServiceRegistry {
    @Override
    public void registry(String serviceName, String serviceAddress) {

    }
}
