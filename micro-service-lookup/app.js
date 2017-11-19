var express = require('express');
var zookeeper = require('node-zookeeper-client');
var httpProxy = require('http-proxy');

var REGISTRY_ROOT = '/registry';
var CONNECTION_STRING = '127.0.0.1:2181';
var PORT = 1234;

// 连接 zookeeper
var zk = zookeeper.createClient(CONNECTION_STRING);
zk.connect();

// 创建代理服务器对象并监听错误事件
var proxy = httpProxy.createProxyServer();
proxy.on('error', function (err, req, res) {
    res.end();
});

// 启动 web 服务器
var app = express();
app.use(express.static('public'));
app.all('*', function (req, res) {
    console.log('request path %s', req.path);
    // 处理图标请求
    if (req.path === '/favicon.ico') {
        res.end();
        return;
    }

    // 获取服务名称
    var serviceName = req.get('Service-Name');
    console.log('Service-Name: %s', serviceName);
    if (!serviceName) {
        console.log('Service-Name request header is not exist');
        res.end();
        return;
    }

    // 获取服务路径
    var servicePath = REGISTRY_ROOT + '/' + serviceName;
    console.log('servicePath: %s', servicePath);

    // 获取服务路径下的节点
    zk.getChildren(servicePath, function (error, addressNodes) {
        if (error) {
            console.log(error.stack);
            res.end();
            return;
        }

        var size = addressNodes.length;
        if (size === 0) {
            console.log('address node is not exist');
            res.end();
            return;
        }

        // 生成地址路径
        var addressPath = servicePath + '/';
        if (size === 1) {
            addressPath += addressNodes[0];
        } else {
            // 若存在多个地址,随机选取一个
            addressPath += addressNodes[parseInt(Math.random() * size)];
        }
        console.log('addressPath: %s', addressPath);

        // 获取服务地址
        zk.getData(addressPath, function (error, serviceAddress) {
            if (error) {
                console.log(error.stack);
                res.end();
                return;
            }
            console.log('serviceAddress: %s', serviceAddress);
            if (!serviceAddress) {
                console.log('service address  is not exist');
                res.end();
            }

            // 执行反向代理
            proxy.web(req, res, {
                target: 'http://' + serviceAddress
            });
        });
    })
});

app.listen(PORT, function () {
    console.log('server is running at %d', PORT)
});