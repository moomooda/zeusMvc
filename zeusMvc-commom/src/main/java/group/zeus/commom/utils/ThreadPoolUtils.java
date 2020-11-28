package group.zeus.commom.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: maodazhan
 * @Date: 2020/11/25 12:34
 */
public class ThreadPoolUtils {

    public static ThreadPoolExecutor makeServerThreadPool(final String serverName, int corePoolSize, int maxPoolSize) {
        ThreadPoolExecutor serverHandlerPool = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "zeusMvc-" + serverName + "-" + r.hashCode());
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );
        return serverHandlerPool;
    }
}
