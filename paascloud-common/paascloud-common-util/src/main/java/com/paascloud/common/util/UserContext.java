package com.paascloud.common.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * 当前登陆用户信息工具类
 * @Auther: yj
 */
public class UserContext {

    public static final String key = "USER_INFO";

    private static ThreadLocal<Map<String, String>> threadLocal = new InheritableThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<String, String>();
        }

    };

    /**
     * 存储用户到当前线程
     * @param value 用户信息json串
     */
    public static String put(String value) {
        return threadLocal.get().put(key, value);
    }

    /**
     * 从当前线程获取用户信息 并通过fastjson转为传入类型对象
     * @param t
     * @param <T>
     */
    public static <T>T get(Class<T> t) {
        try {
            return (T)JSON.parseObject(threadLocal.get().get(key),t);
        }catch (Exception e){
            e.printStackTrace();
        }
         return null;
    }

    /**
     * 从当前线程获取用户信息json串
     */
    public static String get() {
        return threadLocal.get().get(key);
    }

    /**
     * 删除当前线程存储的用户信息
     */
    public static String remove() {
        return threadLocal.get().remove(key);
    }
}
