package com.cvte.yhx.log.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yanghuaxu
 * @date 2023/8/29 17:03
 */
@Slf4j
@Component
public class LogThreadPool implements ApplicationRunner, DisposableBean {

    private static ExecutorService LOG_THREAD_POOL = null;

    @Override
    public void destroy() throws Exception {
        if (LOG_THREAD_POOL != null) {
            LOG_THREAD_POOL.shutdown();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (LOG_THREAD_POOL == null) {
            log.info("开始初始化日志线程池");
            int threads = Runtime.getRuntime().availableProcessors();
            LOG_THREAD_POOL = new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory("COMMON_LOG_POOL", false));
            log.info("初始化日志线程池完成");
        }
    }

    public static Future<?> submit(Runnable runnable) {
        if (LOG_THREAD_POOL != null) {
            return LOG_THREAD_POOL.submit(runnable);
        }
        return null;
    }

    public static <V> Future<V> submit(Callable<V> runnable) {
        if (LOG_THREAD_POOL != null) {
            return LOG_THREAD_POOL.submit(runnable);
        }
        return null;
    }

    public static <T> Future<T> submit(Runnable runnable, T result) {
        if (LOG_THREAD_POOL != null) {
            return LOG_THREAD_POOL.submit(runnable, result);
        }
        return null;
    }


    public static class NamedThreadFactory implements ThreadFactory {
        private static AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final boolean daemon;

        public NamedThreadFactory(String namePrefix, boolean daemon) {
            this.namePrefix = namePrefix;
            this.daemon = daemon;
        }

        public NamedThreadFactory(String namePrefix) {
            this(namePrefix, false);
        }

        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, this.namePrefix + " thread-" + threadNumber.getAndIncrement());
            thread.setDaemon(this.daemon);
            return thread;
        }
    }

}
