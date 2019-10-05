package com.paascloud.common.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class UserContext {

    public static final String key = "USER_INFO";

    private static ThreadLocal<Map<String, String>> threadLocal = new InheritableThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<String, String>();
        }

    };

    public static String put(String value) {
        return threadLocal.get().put(key, value);
    }

    public static <T>T get(Class<T> t) {
        return (T)JSON.parseObject(threadLocal.get().get(key),t);
    }

    public static String get() {
        return threadLocal.get().get(key);
    }

    public static String remove() {
        return threadLocal.get().remove(key);
    }
}
