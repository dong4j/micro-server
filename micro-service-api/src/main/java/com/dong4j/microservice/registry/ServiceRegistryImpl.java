package com.dong4j.microservice.registry;


import com.dong4j.microservice.framework.registry.ServiceRegistry;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dong4j.
 *     email dong4j@gmail.com
 *     date 2017年10月24日 上午10点:34分
 *     describe 服务注册实现
 */
@Component
@Slf4j
public class ServiceRegistryImpl implements ServiceRegistry, Watcher {
    private static CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;
    private static final int SESSION_TIMEOUT = 5000;
    private static final String REGISTRY_PATH = "registry";

    public ServiceRegistryImpl() {
    }

    public ServiceRegistryImpl(String zkServers) {
        try {
            zk = new ZooKeeper(zkServers, SESSION_TIMEOUT, this);
            // 连接 zk 是异步进行的, 创建连接后, 需要等待返回, 所以这里将使用 await() 阻塞执行
            latch.await();
            log.debug("connected to zookeeper");
        } catch (Exception e) {
            log.error("create zookeeper client failure", e);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        // 成功连接 zk 后的回调事件, 如果连接成功, 则继续执行 await 后面的代码
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            latch.countDown();
        }
    }

    /**
     * 服务注册实现
     * @param serviceName    服务名
     * @param serviceAddress 服务地址 ip:port
     */
    @Override
    public void registry(String serviceName, String serviceAddress) {
        try{
            // 创建根节点(持久型)
            String registryPath = REGISTRY_PATH;
            if(zk.exists(registryPath, false) == null){
                zk.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.debug("create registry node: {}", registryPath);
            }

            // 创建服务节点(持久型)
            String servicePath = registryPath + "/" + serviceName;
            if(zk.exists(servicePath, false) == null){
                zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.debug("create service node: {}", servicePath);

            }

            // 创建地址节点(临时有序型)
            String addressPath = servicePath + "/address-";
            String addressNode = zk.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
            log.debug("create address node: {} ==> {}", addressNode, serviceAddress);
        } catch (InterruptedException | KeeperException e) {
            log.error("create node failure", e);
        }
    }
}
