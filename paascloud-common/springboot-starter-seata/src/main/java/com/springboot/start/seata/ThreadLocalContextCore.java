package com.springboot.start.seata;

import io.seata.common.loader.LoadLevel;
import io.seata.core.context.ContextCore;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Thread local context core.
 */
@LoadLevel(name = "ThreadLocalContextCore", order = Integer.MIN_VALUE)
public class ThreadLocalContextCore implements ContextCore {

	private ThreadLocal<Map<String, String>> threadLocal = new InheritableThreadLocal<Map<String, String>>() {
	    @Override
	    protected Map<String, String> initialValue() {
	        return new HashMap<String, String>();
	    }

	};

    @Override
    public String put(String key, String value) {
        return threadLocal.get().put(key, value);
    }

    @Override
    public String get(String key) {
        return threadLocal.get().get(key);
    }

    @Override
    public String remove(String key) {
        return threadLocal.get().remove(key);
    }
}
