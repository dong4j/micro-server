package com.dong4j.microservice.framework.registry;


/**
 * @author dong4j.
 *     email dong4j@gmail.com
 *     date 2017年10月24日 上午10点:34分
 *     describe
 */
public interface ServiceRegistry {
    /**
     * 服务注册接口
     * @param serviceName    服务名
     * @param serviceAddress 服务地址 ip:port
     */
    void registry(String serviceName, String serviceAddress);
}
