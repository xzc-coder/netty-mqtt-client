

\



# netty-mqtt-client

## 1. 介绍

### 1.1 基本概况

该项目是基于Netty实现的MQTT3及MQTT5协议的客户端，创建目的是为了学习和使用MQTT及Netty

### 1.2 技术栈

Java + Netty + MQTT

### 1.3 特色

1.基于高性能的网络开发框架Netty实现，性能更高

2.支持多个客户端使用同一个线程组，支持配置线程数量，占用的资源更少

3.目前支持MQTT 3.1.1以及MQTT 5版本

4.支持单向及双向SSL认证

5.支持自定义实现扩展组件

6.支持组件拦截，可实现插件扩展

7.代码全中文注释

8.支持消息持久化（目前支持Redis、内存、文件），仅保存不清理会话且未完成的客户端消息

9.支持遗嘱消息

10.支持QOS等级为：0、1、2

11.支持MQTT 3.1.1版本和MQTT 5版本相互切换，并且相互兼容

12.支持设置客户端的TCP连接参数

### 1.4 组件介绍

#### MqttConfiguration

​	MQTT全局配置组件，可支持配置TCP连接参数，代理工厂，拦截器，IO线程数，组件创建器及消息存储器

#### MqttClientFactory

​	MQTT客户端工厂，用于创建客户端，只需要传递连接参数，即可根据全局配置创建对应的MQTT客户端

#### MqttMsgStore

​	MQTT消息存储器，默认是用内存消息存储器，如果需要持久化，可使用Redis或文件消息存储器

#### MqttClient

​	MQTT客户端，面向开发者的接口，包含所有的客户端操作API

#### MqttConnectParameter

​	MQTT连接参数，包含MQTT3及MQTT5参数组合，通过设置不同的参数，可创建不同的客户端

#### MqttCallback

​	MQTT回调器，包含MQTT客户端中的所有回调，如消息发送完成回调、消息发送成功回调、连接相关回调、心跳回调、订阅回调等

#### MqttRetrier

​	MQTT重试器，用于重试QOS1及QOS2中失败或未完成的消息，可通过连接配置修改重试时间及间隔

#### MqttDelegateHandler

​	MQTT消息委托器，即MQTT客户端和Netty之间的桥梁，主要是把MQTT的消息和Netty之间的消息进行转换处理

#### MqttConnector

​	MQTT连接器，用于连接MQTT Broker，只负责连接工作

#### MqttChannelHandler

​	MQTT客户端在Netty中的出入栈的处理器，同时负责开启心跳的定时任务

#### MqttMsgIdCache

​	MQTT消息ID缓存器，用于生成MQTT协议层消息的ID

#### ObjectCreator

​	对象创建器，用于创建MqttClient、MqttConnector、MqttDelegateHandler三大组件，可自定义实现三大组件的创建及替换

#### ProxyFactory

​	代理工厂，主要是用于拦截器，支持多种实现，目前支持JDK动态代理以及Cglib动态代理，默认使用JDK动态代理

#### Interceptor

​	拦截器，仅支持拦截MqttClient、MqttConnector、MqttDelegateHandler三大组件，通过注解的方式使用，支持多层级拦截

## 2.使用

### 2.1 依赖

```
<dependency>
    <groupId>io.github.xzc-coder</groupId>
    <artifactId>netty-mqtt-client</artifactId>
    <version>1.1.0</version>
</dependency>
```

### 2.2 初始化

```
//创建MQTT全局配置器（也可以不创建）
MqttConfiguration mqttConfiguration = new MqttConfiguration(2);
//创建MQTT客户端工厂
MqttClientFactory mqttClientFactory = new DefaultMqttClientFactory(mqttConfiguration);
//使用内存消息存储器（默认）
MqttMsgStore mqttMsgStore = new MemoryMqttMsgStore();
mqttClientFactory.setMqttMsgStore(mqttMsgStore);
//创建连接参数，设置客户端ID
MqttConnectParameter mqttConnectParameter = new MqttConnectParameter("netty-mqtt-client-test");
//创建一个客户端
MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
```

### 2.3 连接

#### 连接参数设置

##### MQTT 3

```
//创建连接参数，设置客户端ID
MqttConnectParameter mqttConnectParameter = new MqttConnectParameter("xzc_test");
//设置客户端版本（默认为3.1.1）
mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
//是否自动重连
mqttConnectParameter.setAutoReconnect(true);
//Host
mqttConnectParameter.setHost("broker.emqx.io");
//端口
mqttConnectParameter.setPort(1883);
//是否使用SSL/TLS
mqttConnectParameter.setSsl(false);
//遗嘱消息
MqttWillMsg mqttWillMsg = new MqttWillMsg("test",new byte[]{},MqttQoS.EXACTLY_ONCE);
mqttConnectParameter.setWillMsg(mqttWillMsg);
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

##### MQTT 5

```
//创建连接参数，设置客户端ID
MqttConnectParameter mqttConnectParameter = new MqttConnectParameter("xzc_test");
//设置客户端版本
mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
//是否自动重连
mqttConnectParameter.setAutoReconnect(true);
//Host
mqttConnectParameter.setHost("broker.emqx.io");
//端口
mqttConnectParameter.setPort(1883);
//是否使用SSL/TLS
mqttConnectParameter.setSsl(false);
//遗嘱消息
MqttWillMsg mqttWillMsg = new MqttWillMsg("test",new byte[]{},MqttQoS.EXACTLY_ONCE);
//MQTT 5的遗嘱属性
mqttWillMsg.setResponseTopic("test-response");
mqttWillMsg.setContentType("application/text");
mqttWillMsg.addMqttUserProperty("name","test");
mqttConnectParameter.setWillMsg(mqttWillMsg);
//是否清除会话
mqttConnectParameter.setCleanSession(true);
//心跳间隔
mqttConnectParameter.setKeepAliveTimeSeconds(60);
//连接超时时间
mqttConnectParameter.setConnectTimeoutSeconds(30);
//MQTT 5的连接参数
mqttConnectParameter.setMaximumPacketSize(100);
mqttConnectParameter.setSessionExpiryIntervalSeconds(100);
mqttConnectParameter.addMqttUserProperty("name","test");
//创建一个客户端
MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
//添加回调器
mqttClient.addMqttCallback(new DefaultMqttCallback());
```

#### 连接API

```
/**
 * 进行连接，会阻塞至超时或者连接成功
 */
void connect();

/**
 * 进行连接，不会阻塞
 *
 * @return MqttFutureWrapper
 */
MqttFutureWrapper connectFuture();
```

#### 示例

```
//阻塞连接
mqttClient.connect();
```

```
//非阻塞连接
MqttFutureWrapper mqttFutureWrapper = mqttClient.connectFuture();
//添加监听器
mqttFutureWrapper.addListener(mqttFuture -> {
    if (mqttFuture.isSuccess()) {
        System.out.println("mqtt client connect success");
    } else {
        System.out.println("mqtt client connect failure");
    }
});
```

### 2.4 断开连接

#### 断开连接API

```
/**
 * 断开连接，会阻塞至TCP断开
 */
void disconnect();

/**
 * 断开连接
 *
 * @return Future
 */
MqttFutureWrapper disconnectFuture();

/**
 * 断开连接（MQTT 5）
 *
 * @param mqttDisconnectMsg 断开消息
 * @return Future
 */
MqttFutureWrapper disconnectFuture(MqttDisconnectMsg mqttDisconnectMsg);

/**
 * 断开连接（MQTT 5）
 *
 * @param mqttDisconnectMsg 断开消息
 */
void disconnect(MqttDisconnectMsg mqttDisconnectMsg);
```

#### 示例

##### MQTT 3

```
//阻塞断开连接
mqttClient.disconnect();
```

```
//非阻塞断开连接
MqttFutureWrapper mqttFutureWrapper = mqttClient.disconnectFuture();
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if (mqttFuture.isDone()) {
        System.out.println("mqtt client disconnect done");
    }
});
```

##### MQTT 5

```
//设置MQTT 5的断开连接参数
MqttDisconnectMsg mqttDisconnectMsg = new MqttDisconnectMsg();
mqttDisconnectMsg.setReasonCode((byte) 100);
mqttDisconnectMsg.setReasonString("test disconnect");
mqttDisconnectMsg.setSessionExpiryIntervalSeconds(100);
```

```
//阻塞断开连接
mqttClient.disconnect(mqttDisconnectMsg);
```

```
//非阻塞断开连接
MqttFutureWrapper mqttFutureWrapper = mqttClient.disconnectFuture(mqttDisconnectMsg);
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if (mqttFuture.isDone()) {
        System.out.println("mqtt client disconnect done");
    }
});
```

### 2.5 订阅

#### 订阅API

```
 /**
  * 发送一个订阅消息，会阻塞至发送完成
  *
  * @param topic 订阅的主题
  * @param qos   订阅的qos
  */
 void subscribe(String topic, MqttQoS qos);

 /**
  * 发送一个订阅消息，会阻塞至发送完成（MQTT 5）
  *
  * @param mqttSubInfo 订阅消息
  */
 void subscribe(MqttSubInfo mqttSubInfo);

 /**
  * 发送一个订阅消息，会阻塞至发送完成（MQTT 5）
  *
  * @param mqttSubInfo            订阅消息
  * @param subscriptionIdentifier 订阅标识符
  * @param mqttUserProperties     用户属性
  */
 void subscribe(MqttSubInfo mqttSubInfo, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties);

 /**
  * 发送一个订阅消息，会阻塞至发送完成
  *
  * @param mqttSubInfoList 订阅消息集合
  */
 void subscribes(List<MqttSubInfo> mqttSubInfoList);

 /**
  * 发送一个订阅消息，会阻塞至发送完成（MQTT 5）
  *
  * @param mqttSubInfoList        订阅消息集合
  * @param subscriptionIdentifier 订阅标识符
  * @param mqttUserProperties     用户属性
  */
 void subscribes(List<MqttSubInfo> mqttSubInfoList, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties);

 /**
  * 发送一个订阅消息，会阻塞至发送完成
  *
  * @param topicList 订阅主题集合
  * @param qos       订阅的qos
  */
 void subscribes(List<String> topicList, MqttQoS qos);

 /**
  * 发送一个订阅消息，不会阻塞
  *
  * @param topicList 订阅主题集合
  * @param qos       订阅的qos
  * @return MqttFutureWrapper
  */
 MqttFutureWrapper subscribesFuture(List<String> topicList, MqttQoS qos);

 /**
  * 发送一个订阅消息，不会阻塞
  *
  * @param topic 订阅的主题
  * @param qos   订阅的qos
  * @return MqttFutureWrapper
  */
 MqttFutureWrapper subscribeFuture(String topic, MqttQoS qos);

 /**
  * 发送一个订阅消息，不会阻塞（MQTT 5）
  *
  * @param mqttSubInfo 订阅消息
  * @return MqttFutureWrapper
  */
 MqttFutureWrapper subscribeFuture(MqttSubInfo mqttSubInfo);

 /**
  * 发送一个订阅消息，不会阻塞（MQTT 5）
  *
  * @param mqttSubInfo            订阅消息
  * @param subscriptionIdentifier 订阅标识符
  * @param mqttUserProperties     订阅用户属性
  * @return MqttFutureWrapper
  */
 MqttFutureWrapper subscribeFuture(MqttSubInfo mqttSubInfo, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties);

 /**
  * 发送一个订阅消息，不会阻塞
  *
  * @param mqttSubInfoList        订阅消息集合（MQTT 5）
  * @param subscriptionIdentifier 订阅标识符
  * @param mqttUserProperties     用户属性
  * @return MqttFutureWrapper
  */
 MqttFutureWrapper subscribesFuture(List<MqttSubInfo> mqttSubInfoList, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties);

 /**
  * 发送一个订阅消息，不会阻塞
  *
  * @param mqttSubInfoList 订阅集合
  * @return MqttFutureWrapper
  */
 MqttFutureWrapper subscribesFuture(List<MqttSubInfo> mqttSubInfoList);
```

#### 示例

##### MQTT 3

单个订阅

```
//阻塞订阅
mqttClient.subscribe("test",MqttQoS.EXACTLY_ONCE);
```

```
//非阻塞订阅
MqttFutureWrapper mqttFutureWrapper = mqttClient.subscribeFuture("test", MqttQoS.EXACTLY_ONCE);
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if(mqttFuture.isDone()) {
        System.out.println("mqtt client subscribe done");
    }
});
```

多个订阅

```
//多个订阅主题
List<String> topicList = Arrays.asList("test1", "test2", "test3");
```

```
//阻塞订阅
mqttClient.subscribes(topicList,MqttQoS.EXACTLY_ONCE);
```

```
//非阻塞订阅
MqttFutureWrapper mqttFutureWrapper = mqttClient.subscribesFuture(topicList, MqttQoS.EXACTLY_ONCE);
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if(mqttFuture.isDone()) {
        System.out.println("mqtt client subscribe done");
    }
});
```

##### MQTT 5

单个订阅

```
//MQTT5订阅参数
MqttSubInfo mqttSubInfo = new MqttSubInfo("test",MqttQoS.AT_LEAST_ONCE,true,true, MqttSubscriptionOption.RetainedHandlingPolicy.DONT_SEND_AT_SUBSCRIBE);
MqttProperties.UserProperties userProperties = new MqttProperties.UserProperties();
userProperties.add("name","test");
```

```
//阻塞订阅
mqttClient.subscribe(mqttSubInfo,100,userProperties);
```

```
//非阻塞订阅
MqttFutureWrapper mqttFutureWrapper = mqttClient.subscribeFuture(mqttSubInfoList,100,userProperties);
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if(mqttFuture.isDone()) {
        System.out.println("mqtt client subscribe done");
    }
});
```

多个订阅

```
//MQTT5订阅参数
MqttSubInfo mqttSubInfo = new MqttSubInfo("test",MqttQoS.AT_LEAST_ONCE,true,true, MqttSubscriptionOption.RetainedHandlingPolicy.DONT_SEND_AT_SUBSCRIBE);
MqttProperties.UserProperties userProperties = new MqttProperties.UserProperties();
userProperties.add("name","test");
//多个订阅主题
List<MqttSubInfo> mqttSubInfoList = new ArrayList<>();
mqttSubInfoList.add(mqttSubInfo);
```

```
//阻塞订阅
mqttClient.subscribes(mqttSubInfoList,100,userProperties);
```

```
//非阻塞订阅
MqttFutureWrapper mqttFutureWrapper = mqttClient.subscribesFuture(mqttSubInfoList,100,userProperties);
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if(mqttFuture.isDone()) {
        System.out.println("mqtt client subscribe done");
    }
});
```

### 2.6 取消订阅

#### 取消订阅API

```
 /**
  * 取消订阅，会阻塞至消息发送完成（MQTT 5）
  *
  * @param topicList          取消订阅的主题集合
  * @param mqttUserProperties 用户属性
  */
 void unsubscribes(List<String> topicList, MqttProperties.UserProperties mqttUserProperties);

 /**
  * 取消订阅，会阻塞至消息发送完成
  *
  * @param topicList 取消订阅的主题集合
  */
 void unsubscribes(List<String> topicList);

 /**
  * 取消订阅，会阻塞至消息发送完成（MQTT 5）
  *
  * @param topic              取消订阅的主题
  * @param mqttUserProperties 用户属性
  */
 void unsubscribe(String topic, MqttProperties.UserProperties mqttUserProperties);

 /**
  * 取消订阅，会阻塞至消息发送完成
  *
  * @param topic 取消订阅的主题
  */
 void unsubscribe(String topic);

 /**
  * 取消订阅，不会阻塞（MQTT 5）
  *
  * @param topic              取消订阅的主题
  * @param mqttUserProperties 用户属性
  * @return MqttFutureWrapper
  */
 MqttFutureWrapper unsubscribeFuture(String topic, MqttProperties.UserProperties mqttUserProperties);

 /**
  * 取消订阅，不会阻塞
  *
  * @param topic 取消订阅的主题
  * @return MqttFutureWrapper
  */
 MqttFutureWrapper unsubscribeFuture(String topic);

 /**
  * 取消订阅，不会阻塞
  *
  * @param topicList 取消订阅的主题集合
  * @return MqttFutureWrapper
  */
 MqttFutureWrapper unsubscribesFuture(List<String> topicList);

 /**
  * 取消订阅，不会阻塞（MQTT 5）
  *
  * @param topicList          取消订阅的主题集合
  * @param mqttUserProperties 用户属性
  * @return MqttFutureWrapper
  */
 MqttFutureWrapper unsubscribesFuture(List<String> topicList, MqttProperties.UserProperties mqttUserProperties);

```

#### 示例

##### MQTT 3

单个取消订阅

```
//阻塞取消订阅
mqttClient.unsubscribe("test");
```

```
//非阻塞取消订阅
MqttFutureWrapper mqttFutureWrapper = mqttClient.unsubscribeFuture("test");
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if(mqttFuture.isDone()) {
        System.out.println("mqtt client unsubscribe done");
    }
});
```

多个取消订阅

```
//多个取消订阅的主题
List<String> topicList = Arrays.asList("test1", "test2", "test3");
```

```
//阻塞取消订阅
mqttClient.unsubscribes(topicList);
```

```
//非阻塞取消订阅
MqttFutureWrapper mqttFutureWrapper = mqttClient.unsubscribesFuture(topicList);
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if(mqttFuture.isDone()) {
        System.out.println("mqtt client unsubscribe done");
    }
});
```

##### MQTT 5

单个取消订阅

```
//MQTT 5取消订阅参数
MqttProperties.UserProperties userProperties = new MqttProperties.UserProperties();
userProperties.add("name","test");
```

```
//阻塞取消订阅
mqttClient.unsubscribe("test",userProperties);
```

```
//非阻塞取消订阅
MqttFutureWrapper mqttFutureWrapper = mqttClient.unsubscribeFuture("test",userProperties);
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if(mqttFuture.isDone()) {
        System.out.println("mqtt client unsubscribe done");
    }
});
```

多个取消订阅

```
//MQTT 5取消订阅参数
MqttProperties.UserProperties userProperties = new MqttProperties.UserProperties();
userProperties.add("name","test");
//多个取消订阅主题
List<String> topicList = Arrays.asList("test1", "test2", "test3");
mqttClient.unsubscribes(topicList);
```

```
//阻塞取消订阅
mqttClient.unsubscribes(topicList,userProperties);
```

```
//非阻塞取消订阅
MqttFutureWrapper mqttFutureWrapper = mqttClient.unsubscribesFuture(topicList,userProperties);
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if(mqttFuture.isDone()) {
        System.out.println("mqtt client unsubscribe done");
    }
});
```

### 2.7 发布消息

#### 发布消息API

```
/**
 * 发送一个消息，不会阻塞（MQTT 5）
 *
 * @param mqttMsgInfo mqtt消息
 * @return MqttFutureWrapper
 */
MqttFutureWrapper publishFuture(MqttMsgInfo mqttMsgInfo);

/**
 * 发送一个消息，不会阻塞
 *
 * @param payload 载荷
 * @param topic   主题
 * @param qos     服务质量
 * @param retain  是否保留消息
 * @return MqttFutureWrapper
 */
MqttFutureWrapper publishFuture(byte[] payload, String topic, MqttQoS qos, boolean retain);

/**
 * 发送一个消息，不会阻塞，retain 为 false
 *
 * @param payload 载荷
 * @param topic   主题
 * @param qos     服务质量
 * @return MqttFutureWrapper
 */
MqttFutureWrapper publishFuture(byte[] payload, String topic, MqttQoS qos);

/**
 * 发送一个消息，不会阻塞，retain 为 false，qos 为 0
 *
 * @param payload 载荷
 * @param topic   主题
 * @return MqttFutureWrapper
 */
MqttFutureWrapper publishFuture(byte[] payload, String topic);

/**
 * 发送一个消息，会阻塞至发送完成（MQTT 5）
 *
 * @param mqttMsgInfo mqtt消息
 */
void publish(MqttMsgInfo mqttMsgInfo);

/**
 * 发送一个消息，会阻塞至发送完成
 *
 * @param payload 载荷
 * @param topic   主题
 * @param qos     服务质量
 * @param retain  是否保留消息
 */
void publish(byte[] payload, String topic, MqttQoS qos, boolean retain);

/**
 * 发送一个消息，会阻塞至发送完成，retain 为 false
 *
 * @param payload 载荷
 * @param topic   主题
 * @param qos     服务质量
 */
void publish(byte[] payload, String topic, MqttQoS qos);

/**
 * 发送一个消息，会阻塞至发送完成,retain 为 false，qos 为 0
 *
 * @param payload 载荷
 * @param topic   主题
 */
void publish(byte[] payload, String topic);
```

#### 示例

##### MQTT 3

```
//阻塞发送消息
mqttClient.publish(new byte[]{1,2,3},"test",MqttQoS.EXACTLY_ONCE,true);
```

```
//非阻塞发送消息
MqttFutureWrapper mqttFutureWrapper = mqttClient.publishFuture(new byte[]{1, 2, 3}, "test", MqttQoS.EXACTLY_ONCE, true);
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if(mqttFuture.isDone()) {
        System.out.println("mqtt client publish done");
    }
});
```

##### MQTT 5

```
MqttMsgInfo mqttMsgInfo = new MqttMsgInfo("test",new byte[]{1, 2, 3},MqttQoS.AT_LEAST_ONCE,true);
//MQTT 5发布消息参数
mqttMsgInfo.addMqttUserProperty("name","test");
mqttMsgInfo.setResponseTopic("test-response");
mqttMsgInfo.setContentType("application/text");
mqttMsgInfo.setTopicAlias(10);
```

```
//阻塞发布消息
mqttClient.publish(mqttMsgInfo);
```

```
//非阻塞发送消息
MqttFutureWrapper mqttFutureWrapper = mqttClient.publishFuture(mqttMsgInfo);
//添加监听
mqttFutureWrapper.addListener(mqttFuture -> {
    if(mqttFuture.isDone()) {
        System.out.println("mqtt client publish done");
    }
});
```

### 2.8 回调器

#### API

```
/**
 * 订阅完成回调
 *
 * @param mqttSubscribeCallbackResult 订阅结果
 */
void subscribeCallback(MqttSubscribeCallbackResult mqttSubscribeCallbackResult);

/**
 * 取消订阅完成回调
 *
 * @param mqttUnSubscribeCallbackResult 取消订阅结果
 */
void unsubscribeCallback(MqttUnSubscribeCallbackResult mqttUnSubscribeCallbackResult);

/**
 * 当发送的消息，完成时回调
 *
 * @param mqttSendCallbackResult 发送消息结果
 */
void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult);

/**
 * 接收消息完成时回调
 *
 * @param receiveCallbackResult 接收消息结果
 */
void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult);

/**
 * TCP的连接成功时回调
 *
 * @param mqttConnectCallbackResult TCP的连接成功结果
 */
void channelConnectCallback(MqttConnectCallbackResult mqttConnectCallbackResult);

/**
 * MQTT连接完成时回调
 *
 * @param mqttConnectCallbackResult 连接完成结果
 */
void connectCompleteCallback(MqttConnectCallbackResult mqttConnectCallbackResult);

/**
 * 连接丢失时回调
 *
 * @param mqttConnectLostCallbackResult 连接丢失结果
 */
void connectLostCallback(MqttConnectLostCallbackResult mqttConnectLostCallbackResult);

/**
 * 收到心跳响应时回调
 *
 * @param mqttHeartbeatCallbackResult 心跳响应结果
 */
void heartbeatCallback(MqttHeartbeatCallbackResult mqttHeartbeatCallbackResult);

/**
 * Netty的Channel发生异常时回调
 *
 * @param mqttConnectParameter               连接时的参数
 * @param mqttChannelExceptionCallbackResult Channel异常结果
 */
void channelExceptionCaught(MqttConnectParameter mqttConnectParameter, MqttChannelExceptionCallbackResult mqttChannelExceptionCallbackResult);
```

#### 示例

```
mqttClient.addMqttCallback(new MqttCallback() {
    @Override
    public void connectCompleteCallback(MqttConnectCallbackResult mqttConnectCallbackResult) {
        if(mqttConnectCallbackResult.getCause() != null) {
            //连接成功时，订阅主题
            mqttClient.subscribe("test",MqttQoS.EXACTLY_ONCE);
        }
    }
});
```

### 2.9 拦截器

#### 步骤

支持拦截的接口：MqttClient、MqttConnector、MqttDelegateHandler

使用方式：

​	1.实现拦截器接口Interceptor

​	2.类上添加注解@Intercepts，并在type值中添加支持拦截的接口，直接单个和多个

​	3.在intercept方法中进行拦截

​	4.调用Invocation的proceed()执行目标方法

​	5.添加拦截器值MQTT客户端工厂或全局配置器中

#### 示例

```
@Intercepts(type = {MqttClient.class, MqttConnector.class})
public class LogInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();
        Method method = invocation.getMethod();
        //执行目标方法
        Object returnObj = invocation.proceed();
        LogUtils.info(LogInterceptor.class, "拦截目标：" + target.getClass().getSimpleName() + "，拦截方法：" + method.getName() + "，拦截参数：" + Arrays.toString(args) + "，拦截返回值：" + returnObj);
        return returnObj;
    }
}
```

```
//通过MQTT客户端工厂添加拦截器
mqttClientFactory.addInterceptor(new LogInterceptor());
```

或者

```
//通过全局配置器添加拦截器
mqttConfiguration.addInterceptor(new LogInterceptor());
```

### 2.10 消息存储器

目前支持三种消息存储方式

#### 内存消息存储器（默认）

```
mqttClientFactory.setMqttMsgStore(new MemoryMqttMsgStore());
```

使用该方式，未完成的QOS1、QOS2的消息在JVM重启后会消失

#### Redis消息存储器

导入Redis的maven依赖

```
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>5.1.0</version>
</dependency>
```

使用Redis持久化消息存储器

```
JedisPool jedisPool = new JedisPool();
RedisMqttMsgStore redisMqttMsgStore = new RedisMqttMsgStore(jedisPool);
mqttClientFactory.setMqttMsgStore(redisMqttMsgStore);
```

使用该方式，未完成的QOS1、QOS2的消息会存储（仅限classSession为false生效）

#### 文件消息存储器

```
File mqttMsgFile = new File("E:/test.properties");
if(!mqttMsgFile.exists()) {
    mqttMsgFile.createNewFile();
}
FileMqttMsgStore fileMqttMsgStore = new FileMqttMsgStore(mqttMsgFile);
mqttClientFactory.setMqttMsgStore(fileMqttMsgStore);
```

使用该方式，必须传递一个properties的文件；该方式下未完成的QOS1、QOS2的消息会存储（仅限classSession为false生效）

### 2.11 代理工厂

#### 使用

目前已有两种代理工厂的实现，包括：JDK的动态代理（默认）、Cglib的动态代理

如果需要切换为cglib的动态代理，需要先导入cglib的maven依赖

```
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>3.3.0</version>
</dependency>
```

```
mqttClientFactory.setProxyFactory(new CglibProxyFactory());
```

#### 扩展

如果需要自行实现代理工厂，只需实现 ProxyFactory 接口即可

### 2.12 消息别名

当短时间内需要给同一个主题发送大量消息时，可以使用消息别名的方式（MQTT 5）

```
MqttMsgInfo mqttMsgInfo = new MqttMsgInfo("test",new byte[]{1,2,3},MqttQoS.EXACTLY_ONCE);
//消息别名
mqttMsgInfo.setTopicAlias(101);
MqttMsgInfo mqttMsgInfo1 = new MqttMsgInfo("test",new byte[]{1,2,3},MqttQoS.EXACTLY_ONCE);
//消息别名
mqttMsgInfo.setTopicAlias(101);
mqttClient.publish(mqttMsgInfo);
mqttClient.publish(mqttMsgInfo1);
//...更多同一主题的消息
```

只需要为同一消息主题设置相同的别名，再发送消息时，会无感的将主题名替换为 null ，从而节省主题流量。

### 2.13 认证增强（MQTT 5）

在连接参数设置时，添加连接时的认证内容，MQTT客户端会在收到 auth 包时，调用认证器的方法

```
MqttConnectParameter mqttConnectParameter = new MqttConnectParameter("test");
//添加认证方法和认证数据
mqttConnectParameter.setAuthenticationMethod("test");
mqttConnectParameter.setAuthenticationData(new byte[] {1,1,1});
//添加认证器
MqttAuthenticator mqttAuthenticator = (s, bytes) -> {
    //对bytes 数组处理....
    //返回认证指示
    MqttAuthInstruct mqttAuthInstruct = new MqttAuthInstruct(MqttAuthInstruct.Instruct.AUTH_CONTINUE);
    mqttAuthInstruct.setAuthenticationData(new byte[]{1,2,3});
    return mqttAuthInstruct;
};
mqttConnectParameter.setMqttAuthenticator(mqttAuthenticator);
```

开发者可根据接收到的认证数据进行下一步操作的判断，并且指示下一步的认证操作，当MQTT Broker认证完成后，将会执行连接完成回调



### 2.14 配置参数

#### 全局配置参数（MqttConfiguration）

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

#### MQTT连接参数（MqttConnectParameter）

| 字段/方法                                     | 类型              | 默认值     | 说明                                                         |
| --------------------------------------------- | ----------------- | ---------- | ------------------------------------------------------------ |
| clientId                                      | String            | 无         | 客户端ID，不能为null，也不能重复                             |
| mqttVersion                                   | MqttVersion       | MQTT_3_1_1 | 客户端版本号                                                 |
| host                                          | String            | localhost  | MQTTBroker的host                                             |
| port                                          | int               | 1883       | MQTTBroker的端口                                             |
| username                                      | String            | 无         | MQTT的连接账号                                               |
| password                                      | char[]            | 无         | MQTT的连接密码                                               |
| willMsg                                       | MqttWillMsg       | 无         | MQTT的遗嘱消息                                               |
| retryIntervalMillis                           | long              | 1000毫秒   | 消息重试器的重试间隔，单位毫秒                               |
| retryIntervalIncreaseMillis                   | long              | 1000毫秒   | 每次消息重试失败时，增大其重试间隔值，单位毫秒               |
| retryIntervalMaxMillis                        | long              | 15000毫秒  | 重试间隔的最大值，单位毫秒                                   |
| keepAliveTimeSeconds                          | int               | 30秒       | MQTT心跳间隔，单位秒                                         |
| keepAliveTimeCoefficient                      | BigDecimal        | 0.75       | MQTT心跳间隔系数，由于某些Broker读超时时间为心跳间隔时间，中间发报文需要时间，可能在网络不好的情况下会导致超时，所以增加该系数，即发送心跳的时间为 心跳间隔 * 系数 ，默认0.75 |
| connectTimeoutSeconds                         | long              | 30秒       | MQTT连接超时时间，单位秒                                     |
| autoReconnect                                 | boolean           | false      | 是否自动重连                                                 |
| cleanSession                                  | boolean           | true       | 是否清理会话                                                 |
| ssl                                           | boolean           | false      | 是否开启SSL/TLS                                              |
| rootCertificateFile                           | File              | 无         | 根证书文件                                                   |
| clientPrivateKeyFile                          | File              | 无         | 客户端私钥文件，双向SSL时需要                                |
| clientCertificateFile                         | File              | 无         | 客户端证书文件，双向SSL时需要                                |
| sessionExpiryIntervalSeconds                  | int               | 无         | 会话过期时间，单位秒，MQTT 5                                 |
| authenticationMethod                          | String            | 无         | 认证方法，MQTT 5                                             |
| authenticationData                            | byte[]            | 无         | 认证数据，MQTT 5                                             |
| requestProblemInformation                     | int               | 1          | 请求问题信息标识符，MQTT 5                                   |
| requestResponseInformation                    | int               | 0          | 请求响应标识，MQTT 5                                         |
| responseInformation                           | String            | 无         | 响应信息，MQTT 5                                             |
| receiveMaximum                                | int               | 无         | 接收最大数量，MQTT 5                                         |
| topicAliasMaximum                             | int               | 无         | 主题别名最大长度，MQTT 5                                     |
| maximumPacketSize                             | int               | 无         | 最大报文长度，MQTT 5                                         |
| addMqttUserProperty(String key, String value) | String、String    | 无         | 添加一个用户属性，MQTT 5                                     |
| mqttAuthenticator                             | MqttAuthenticator | 无         | 认证器，MQTT 5                                               |

注意：在SSL相关的参数中，rootCertificateFile不是必须的，前提是 Broker 的证书是权威CA认证的话就不需要，如果是自签名的证书就需要该文件；并且在双向认证中，如果你使用的是jks或pkcs后缀的文件（私钥和证书的结合体），那么请将其转换为证书和私钥两个文件。

### 2.15 测试用例

所有的测试用例均在目录：

> src/test/java/io/github/netty/mqtt/client

当需要执行测试用例时，需要修改pom.xml文件中的，编译插件项，如下：

```
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                    <!-- 此处修改为false -->
                    <skip>false</skip>
                </configuration>
            </plugin>
```



## 3. 其它

### 3.1 注意事项

1.需要JDK版本1.8及以上

2.日志需要导入日志框架，如果没有日志框架，则会在控制台打印日志

3.以上所有的API，MQTT 3.1.1 和 MQTT 5版本之间是互相兼容的

### 3.2 issue

如果产生问题，请提issue

格式：问题描述+复现代码示例
