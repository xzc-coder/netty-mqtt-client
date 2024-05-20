# netty-mqtt-client

## 1. 介绍

### 1.1 基本概况

该项目是基于Netty实现的MQTT协议的客户端，创建目的是为了学习和使用MQTT协议及Netty

### 1.2 技术栈

Java + Netty + MQTT

### 1.3 组件介绍

MqttConfiguration：MQTT全局配置组件，可支持配置TCP连接参数，代理工厂，拦截器，IO线程数，组件创建器及消息存储器

MqttClientFactory：MQTT客户端工厂，用于创建客户端，只需要传递连接参数，即可根据全局配置创建对应的MQTT客户端

MqttMsgStore：MQTT消息存储器，默认是用内存存储器，如果需要持久化，可自行实现

MqttClient：MQTT客户端，面向用户的API接口。

MqttConnectParameter：MQTT连接参数，通过设置不同的参数，可创建不同的客户端

MqttCallback：MQTT回调器，包含MQTT协议中的所有回调

MqttRetrier：MQTT重试器，重试QOS1和QOS2的消息

MqttDelegateHandler：MQTT消息委托器，即MQTT客户端和Netty之间的桥梁，主要是把MQTT的消息和Netty之间的消息进行转换处理

MqttConnector：MQTT连接器，用于连接MQTTBroker

MqttChannelHandler：MQTT在Netty中的出入栈处理器

MqttMsgIdCache：MQTT消息ID缓存器，用于生成MQTT协议层消息的ID

ObjectCreator：对象创建器，支持自定义创建MqttClient、MqttConnector、MqttDelegateHandler三大组件的实现

ProxyFactory：代理工厂，主要是用于拦截器，支持多种实现，默认使用JDK动态代理的实现

Interceptor：拦截器，仅支持拦截MqttClient、MqttConnector、MqttDelegateHandler三大接口

### 1.4 特色

1.基于高性能的网络开发框架Netty实现，性能更高

2.支持多个客户端使用同一个线程组，支持配置线程数量，占用的资源更少

3.目前支持MQTT 3.1版本（后续会开发5.0版本）

4.支持单向SSL

5.支持自定义实现扩展组件

6.支持组件拦截

7.代码全中文注释

### 1.5 示例

#### 客户端操作

初始化：

```
        MqttClientFactory mqttClientFactory = new DefaultMqttClientFactory();
        MqttMsgStore mqttMsgStore = new MemoryMqttMsgStore();
        mqttClientFactory.setMqttMsgStore(mqttMsgStore);
        //创建连接参数，设置客户端ID
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter("xzc_test");
        //是否自动重连
        mqttConnectParameter.setAutoReconnect(true);
        //Host
        mqttConnectParameter.setHost("broker.emqx.io");
        //端口
        mqttConnectParameter.setPort(1883);
        //是否使用SSL/TLS
        mqttConnectParameter.setSsl(false);
        //是否清除会话
        mqttConnectParameter.setCleanSession(true);
        //心跳间隔
        mqttConnectParameter.setKeepAliveTimeSeconds(60);
        //连接超时时间
        mqttConnectParameter.setConnectTimeoutSeconds(30);
        //创建一个客户端
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        //添加回调器
        mqttClient.addMqttCallback(new DefaultMqttCallback());
```

连接：

```
        //阻塞连接至完成或超时
        mqttClient.connect();
```

```
		//非阻塞连接
		MqttFutureWrapper connectFuture = mqttClient.connectFuture();
		//阻塞至连接完成
        connectFuture.awaitComplete();
```

断开连接：

```
        //阻塞断开连接至完成
        mqttClient.disconnect();
```

```
		//非阻塞断开连接
		MqttFutureWrapper disconnectFuture = mqttClient.disconnectFuture();
		//阻塞至断开连接完成
        disconnectFuture.awaitComplete();
```

关闭客户端：

    		//关闭客户端，关闭之后无法再进行操作
    		mqttClient.close();

订阅：

```
        //阻塞订阅至完成
        mqttClient.subscribe("testMqttClient", MqttQoS.AT_LEAST_ONCE);
```

```
        //非阻塞订阅
        MqttFutureWrapper subscribeFuture = mqttClient.subscribeFuture("testMqttClient", MqttQoS.AT_LEAST_ONCE);
        //阻塞至订阅完成
        subscribeFuture.awaitComplete();
```

取消订阅：

```
        //阻塞取消订阅至完成
        mqttClient.unsubscribe("testMqttClient");
```

```
        //非阻塞取消订阅
        MqttFutureWrapper unsubscribeFuture = mqttClient.unsubscribeFuture("testMqttClient");
        //阻塞至取消订阅完成
        unsubscribeFuture.awaitComplete();
```

发送消息：

```
        //阻塞发送消息至完成
        mqttClient.publish("hello world!".getBytes(StandardCharsets.UTF_8), "testMqttClient", MqttQoS.EXACTLY_ONCE);
```

```
        //非阻塞发送消息
        MqttFutureWrapper publishFuture = mqttClient.publishFuture("hello world!".getBytes(StandardCharsets.UTF_8), "testMqttClient", MqttQoS.EXACTLY_ONCE);
        //阻塞至发送消息完成
        publishFuture.awaitComplete();
```

#### 拦截器操作

支持拦截的接口：MqttClient、MqttConnector、MqttDelegateHandler

使用方式：

​	1.实现拦截器接口Interceptor

​	2.类上添加注解@Intercepts，并在type值中添加支持拦截的接口

​	3.在intercept方法中进行拦截

​	4.调用Invocation的proceed()执行目标方法

代码：

```
@Intercepts(type = {MqttClient.class})
public class LogInterceptorExample implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();
        Method method = invocation.getMethod();
        //执行目标方法
        Object returnObj = invocation.proceed();
        LogUtils.info(LogInterceptorExample.class, "拦截目标：" + target.getClass().getSimpleName() + "，拦截方法：" + method.getName() + "，拦截参数：" + Arrays.toString(args) + "，拦截返回值：" + returnObj.toString());
        return returnObj;
    }
}

```

更多示例请参考包：com.github.netty.mqtt.client.example

### 1.6 配置参数说明

MqttConfiguration配置参数：

| 字段/方法                                  | 类型                  | 默认值                           | 说明                                                         |
| ------------------------------------------ | --------------------- | -------------------------------- | ------------------------------------------------------------ |
| proxyFactory                               | ProxyFactory          | JdkProxyFactory                  | 代理工厂，用于创建三大组件（MqttClient、MqttConnector、MqttDelegateHandler）的代理对象 |
| maxThreadNumber                            | int                   | 1                                | 处理IO的最大线程数即NioEventLoopGroup中的线程数量，多个客户端时可以设置为多个 |
| mqttClientObjectCreator                    | ObjectCreator         | MqttClientObjectCreator          | MQTT客户端的对象创建器                                       |
| mqttConnectorObjectCreator                 | ObjectCreator         | MqttConnectorObjectCreator       | MQTT连接器的对象创建器                                       |
| mqttDelegateHandlerObjectCreator           | ObjectCreator         | MqttDelegateHandlerObjectCreator | MQTT委托处理器的对象创建器                                   |
| mqttMsgStore                               | MqttMsgStore          | MemoryMqttMsgStore               | MQTT消息存储器                                               |
| option(ChannelOption option, Object value) | ChannelOption、Object | 无                               | Netty中的TCP连接参数                                         |
| addInterceptor(Interceptor interceptor)    | Interceptor           | 无                               | 拦截器，用于拦截MqttClient、MqttConnector、MqttDelegateHandler |

注意：MqttClientFactory中的配置，会放入到MqttConfiguration中。



MqttConnectParameter配置参数：

| 字段/方法             | 类型        | 默认值     | 说明                             |
| --------------------- | ----------- | ---------- | -------------------------------- |
| clientId              | String      | 无         | 客户端ID，不能为null，也不能重复 |
| mqttVersion           | MqttVersion | MQTT_3_1_1 | 客户端版本号                     |
| host                  | String      | localhost  | MQTTBroker的host                 |
| port                  | int         | 1883       | MQTTBroker的端口                 |
| username              | String      | 无         | MQTT的连接账号                   |
| password              | char[]      | 无         | MQTT的连接密码                   |
| willMsg               | MqttWillMsg | 无         | MQTT的遗嘱消息                   |
| retryIntervalMillis   | long        | 1000毫秒   | 消息重试器的重试间隔，单位毫秒   |
| keepAliveTimeSeconds  | int         | 30秒       | MQTT心跳间隔，单位秒             |
| connectTimeoutSeconds | long        | 30秒       | MQTT连接超时时间，单位秒         |
| autoReconnect         | boolean     | false      | 是否自动重连                     |
| cleanSession          | boolean     | true       | 是否清理会话                     |
| ssl                   | boolean     | false      | 是否开启单向SSL/TLS              |

## 2. 其它

### 2.1 注意事项

1.需要JDK版本1.8及以上

2.日志需要导入日志框架，如果没有日志框架，则会在控制台打印日志

3.如需打包为jar包，可自行用maven插件或maven命令打包

### 2.2 issue

如果产生问题，请提issue

格式：问题描述+复现代码示例
