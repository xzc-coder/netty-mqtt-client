package io.github.netty.mqtt.client.support.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 反射工具类
 * @author: xzc-coder
 */
public class ReflectionUtils {

    private ReflectionUtils() {
    }


    /**
     * 获取类的所有接口
     *
     * @param clazz 类信息
     * @return 所有接口
     */
    public static Class[] getAllInterfaces(Class clazz) {
        List<Class> interfaceList = new ArrayList<>();
        if (clazz != null) {
            interfaceList.addAll(Arrays.asList(clazz.getInterfaces()));
            Class superclass = clazz.getSuperclass();
            //递归调用
            Class[] superInterfaces = getAllInterfaces(superclass);
            interfaceList.addAll(Arrays.asList(superInterfaces));
        }
        return interfaceList.toArray(new Class[interfaceList.size()]);
    }

    /**
     * 创建一个实例
     *
     * @param className 全类名
     * @param args 构建参数
     * @return 实例对象
     */
    public static Object createInstance(String className, Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            Class<?>[] parameterClass = getParameterClass(args);
            Constructor constructor = clazz.getConstructor(parameterClass);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取参数的Class数组
     * @param args 参数
     * @return Class数组
     */
    public static Class<?>[] getParameterClass(Object... args) {
        if (args == null) {
            return null;
        }
        Class<?>[] parameterClass = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterClass[i] = args[i].getClass();
        }
        return parameterClass;
    }
}
