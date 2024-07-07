package io.github.netty.mqtt.client.constant;


/**
 * MQTT消息方向
 * @author: xzc-coder
 */
public enum MqttMsgDirection {

    /**
     * 发送方消息
     */
    SEND(0),
    /**
     * 接收方消息
     */
    RECEIVE(1);

    /**
     * 方向值
     */
    private final int direction;

    MqttMsgDirection(int direction) {
        this.direction = direction;
    }

    /**
     * 根据方向值查询枚举值
     * @param direction 方向值
     * @return MqttMsgDirection
     */
    public static MqttMsgDirection findMqttMsgDirection(int direction) {
        MqttMsgDirection[] mqttMsgDirections = MqttMsgDirection.values();
        for(MqttMsgDirection mqttMsgDirection : mqttMsgDirections) {
            if(mqttMsgDirection.direction == direction){
                return mqttMsgDirection;
            }
        }
        return null;
    }

    public int getDirection() {
        return direction;
    }
}
