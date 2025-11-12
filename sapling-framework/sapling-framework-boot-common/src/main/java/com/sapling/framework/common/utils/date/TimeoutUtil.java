package com.sapling.framework.common.utils.date;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @Description 超时机制
 * @author mbws
 **/
public class TimeoutUtil {

    /**
     * 执行用户回调接口的 线程池;
     * 计算回调接口的超时时间
     */
    private static ExecutorService executorService = new ThreadPoolExecutor(
        8,
        64,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setDaemon(true).setNameFormat("TimeoutUtil-Thread-%d").build(),
        new ThreadPoolExecutor.DiscardPolicy()
    );

    /**
     * 有超时时间的方法
     * @param timeout 时间秒
     * @return
     */
    public static void timeoutMethod(long timeout, Function function) throws InterruptedException, ExecutionException, TimeoutException {
        FutureTask futureTask = new FutureTask(()->(function.apply("")));
        executorService.execute(futureTask);
        try {
            futureTask.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            futureTask.cancel(true);
            throw e;
        }

    }
}
