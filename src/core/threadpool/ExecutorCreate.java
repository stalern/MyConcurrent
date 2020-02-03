package core.threadpool;

import base.createthread.ImplRunnable;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 线程池的使用方法
 * @author stalern
 * @date 2020/01/29~14:47
 */
public class ExecutorCreate {
    private static final int MAX_POOL_SIZE = 10;
    private static final int CORE_POOL_SIZE = 5;
    private static final int QUEUE_CAPACITY = 1024;
    private static final Long KEEP_ALIVE_TIME = 1L;
    /**
     * 1
     */
    private static ExecutorService executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 2. 默认不建议用
     */
    private static ExecutorService executorService1 = Executors.newFixedThreadPool(100);
    private static ExecutorService executorService2 = Executors.newSingleThreadExecutor();
    private static ExecutorService executorService3 = Executors.newCachedThreadPool();
    private static ScheduledExecutorService executorService4 = Executors.newScheduledThreadPool(1);
    private static ExecutorService executorService5 = Executors.newWorkStealingPool();

    /**
     * 3
     */
    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();
    private static ExecutorService pool = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY),
            namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        executor.execute(new ImplRunnable());
        executorService1.execute(new ImplRunnable());
        executorService2.execute(new ImplRunnable());
        executorService3.execute(new ImplRunnable());
        // 只执行一次
        executorService4.schedule(new ImplRunnable(),20L,TimeUnit.MINUTES);
        // 每5分钟执行一次
        executorService4.scheduleAtFixedRate(new ImplRunnable(),20L,5L,TimeUnit.MINUTES);
        executorService5.execute(new ImplRunnable());
        pool.submit(new ImplRunnable());

    }
}
