package com.github.netty.mqtt.client;

import com.github.netty.mqtt.client.constant.MqttVersion;
import com.github.netty.mqtt.client.support.util.EmptyUtils;
import com.github.netty.mqtt.client.util.PropertiesUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

/**
 * @Date: 2024/6/29 11:40
 * @Description: SSL相关的测试用例
 * @author: xzc-coder
 */
@RunWith(JUnit4.class)
public class SslTest {

    private static MqttClientFactory mqttClientFactory = new DefaultMqttClientFactory();

    @BeforeClass
    public static void beforeClass() throws IOException {
        PropertiesUtils.loadTestProperties();
    }

    @AfterClass
    public static void afterClass() {
        mqttClientFactory.close();
    }

    @Test
    public void testSingleSsl() {
        String host = PropertiesUtils.getHost();
        int serverSslPort = PropertiesUtils.getServerSslPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(serverSslPort);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        mqttConnectParameter.setSsl(true);
        //如果是权威CA颁发的证书则不需要导入证书
        File rootCertificateFile = getRootCertificateFile();
        if (rootCertificateFile != null) {
            mqttConnectParameter.setRootCertificateFile(rootCertificateFile);
        }
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        try {
            mqttClient.connect();
        } catch (Exception e) {
            Assert.fail("clientId connect failed,cause : " + e.getMessage());
        }
        Assert.assertTrue(mqttClient.isOnline());
    }

    /**
     * 该测试需要搭建Broker，并且生成对应的证书及导入到Broker中，过于麻烦，
     * 需要进行该测试的自行搭建测试
     */
    @Test
    @Ignore
    public void testTwoWaySsl() {
        String host = PropertiesUtils.getHost();
        int serverSslPort = PropertiesUtils.getServerSslPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(serverSslPort);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        mqttConnectParameter.setSsl(true);
        //如果是权威CA颁发的证书则不需要导入证书
        File rootCertificateFile = getRootCertificateFile();
        if (rootCertificateFile != null) {
            mqttConnectParameter.setRootCertificateFile(rootCertificateFile);
        }
        //双向认证需要的，客户端的证书
        File clientCertificateFile = getClientCertificateFile();
        if (clientCertificateFile != null) {
            mqttConnectParameter.setClientCertificateFile(clientCertificateFile);
        }
        //双向认证需要的，客户端的私钥
        File clientPrivateKeyFile = getClientPrivateKeyFile();
        if (clientPrivateKeyFile != null) {
            mqttConnectParameter.setClientPrivateKeyFile(clientPrivateKeyFile);
        }
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        try {
            mqttClient.connect();
        } catch (Exception e) {
            Assert.fail("clientId connect failed,cause : " + e.getMessage());
        }
        Assert.assertTrue(mqttClient.isOnline());
    }

    /**
     * 获取Broker的根证书文件
     *
     * @return Broker的根证书文件
     */
    private static File getRootCertificateFile() {
        String rootCertificateFileName = PropertiesUtils.getRootCertificateFileName();
        if (EmptyUtils.isNotBlank(rootCertificateFileName)) {
            String path = SslTest.class.getClassLoader().getResource("").getPath();
            path = path + File.separator + rootCertificateFileName;
            return new File(path);
        } else {
            return null;
        }
    }

    /**
     * 获取客户端的证书文件
     *
     * @return 客户端的证书文件
     */
    public static File getClientCertificateFile() {
        String clientCertificateFileName = PropertiesUtils.getClientCertificateFileName();
        if (EmptyUtils.isNotBlank(clientCertificateFileName)) {
            String path = SslTest.class.getClassLoader().getResource("").getPath();
            path = path + File.separator + clientCertificateFileName;
            return new File(path);
        } else {
            return null;
        }
    }

    /**
     * 获取客户端的私钥文件
     *
     * @return 客户端的私钥
     */
    public File getClientPrivateKeyFile() {
        String clientPrivateKeyFileName = PropertiesUtils.getClientPrivateKeyFileName();
        if (EmptyUtils.isNotBlank(clientPrivateKeyFileName)) {
            String path = SslTest.class.getClassLoader().getResource("").getPath();
            path = path + File.separator + clientPrivateKeyFileName;
            return new File(path);
        } else {
            return null;
        }
    }
}
