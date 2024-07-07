package io.github.netty.mqtt.client.constant;

/**
 * MQTT消息状态
 * @author: xzc-coder
 */
public enum MqttMsgState {

    /**
     * 发布消息
     */
    PUBLISH(0),
    /**
     * 发布确认消息
     */
    PUBACK(1),
    /**
     * PUBREC，QoS 2，第一步
     */
    PUBREC(2),
    /**
     * 发布释放,QoS 2，第二步
     */
    PUBREL(3),
    /**
     * 发布完成，QoS 2，第三步
     */
    PUBCOMP(4),
    /**
     * 无效的，表示该消息没用，占用判断使用
     */
    INVALID(5),
    ;

    /**
     * 状态值
     */
    private final int state;

    MqttMsgState(int state) {
        this.state = state;
    }

    /**
     * 根据状态值查询枚举值
     * @param state 状态值
     * @return MqttMsgState
     */
    public static MqttMsgState findMqttMsgState(int state) {
        MqttMsgState[] mqttMsgStates = MqttMsgState.values();
        for (MqttMsgState mqttMsgState : mqttMsgStates) {
            if (mqttMsgState.state == state) {
                return mqttMsgState;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }
}
