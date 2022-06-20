package com.yhx.log.context;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * 当前线程上下文容器
 * @author yanghuaxu
 * @date 2022/6/6 19:57
 */
public class OperationRecordContext {

    protected static final InheritableThreadLocal<Deque<Map<String, Object>>> recordContext = new InheritableThreadLocal<>();

    public static void putEmptyContext() {
        Deque<Map<String, Object>> deque = recordContext.get();
        if (deque == null) {
            deque = new ArrayDeque<>();
            recordContext.set(deque);
        }
        recordContext.get().push(new HashMap<>());
    }

    public static void set(String key, Object value) {
        Deque<Map<String, Object>> deque = recordContext.get();
        if (deque == null) {
            deque = new ArrayDeque<>();
            recordContext.set(deque);
        }
        Deque<Map<String, Object>> stack = recordContext.get();
        if (stack.size() == 0) {
            stack.push(new HashMap<>());
        }
        stack.element().put(key, value);
    }

    public static Object get(String key) {
        Map<String, Object> map = recordContext.get().peek();
        return map == null ? null : map.get(key);
    }

    public static Map<String, Object> get() {
        return recordContext.get().peek();
    }

    public static void destroy() {
        if (recordContext.get() != null) {
            recordContext.get().pop();
        }
    }

}
