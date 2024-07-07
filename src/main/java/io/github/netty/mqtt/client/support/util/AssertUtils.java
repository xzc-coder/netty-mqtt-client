package io.github.netty.mqtt.client.support.util;

import java.util.Collection;
import java.util.Map;

/**
 * 断言工具类
 * @author: xzc-coder
 */
public class AssertUtils {

    private AssertUtils() {
    }


    public static void notEmpty(CharSequence charSequence, String message) {
        if (EmptyUtils.isEmpty(charSequence)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notBlank(CharSequence charSequence, String message) {
        if (EmptyUtils.isBlank(charSequence)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Collection collection, String message) {
        if (EmptyUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Map map, String message) {
        if (EmptyUtils.isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Object[] array, String message) {
        if (EmptyUtils.isEmpty(array)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object obj, RuntimeException exeception) {
        if (obj == null) {
            throw exeception;
        }
    }
}
