package io.github.netty.mqtt.client.util;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 测试用的Properties工具类
 * @author: xzc-coder
 */
public class PropertiesUtils {

    private static final String TEST_NAME = "test.properties";

    private static final String HOST_KEY = "host";

    private static final String PORT_KEY = "port";

    private static final String CLIENT_ID_KEY = "clientId";

    private static final String SERVER_SSL_PORT_KEY = "serverSslPort";

    private static final String ROOT_CERTIFICATE_FILE_NAME_KEY = "rootCertificateFileName";


    private static final String CLIENT_CERTIFICATE_FILE_NAME_KEY = "clientCertificateFileName";

    private static final String CLIENT_PRIVATE_KEY_FILE_NAME_KEY = "clientPrivateKeyFileName";

    private static final Properties properties = new Properties();

    private PropertiesUtils() {
    }

    public static String getHost() {
        return properties.getProperty(HOST_KEY);
    }

    public static int getPort() {
        return Integer.parseInt(properties.getProperty(PORT_KEY));
    }

    public static String getClientId() {
        return properties.getProperty(CLIENT_ID_KEY);
    }

    public static int getServerSslPort() {
        return Integer.parseInt(properties.getProperty(SERVER_SSL_PORT_KEY));
    }

    public static String getRootCertificateFileName() {
        return properties.getProperty(ROOT_CERTIFICATE_FILE_NAME_KEY);
    }

    public static String getClientCertificateFileName() {
        return properties.getProperty(CLIENT_CERTIFICATE_FILE_NAME_KEY);
    }

    public static String getClientPrivateKeyFileName() {
        return properties.getProperty(CLIENT_PRIVATE_KEY_FILE_NAME_KEY);
    }

    public static void loadTestProperties() throws IOException {
        String path = PropertiesUtils.class.getClassLoader().getResource("").getPath();
        path = path + File.separator + TEST_NAME;
        properties.load(Files.newInputStream(new File(path).toPath()));
    }

}
