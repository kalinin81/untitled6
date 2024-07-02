package org.example;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4,
                5,
                5,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(4),
                runnable -> {
                    Thread thread = new Thread(runnable);
                    System.out.printf("Create new thread %s\n", thread.getName());
                    return thread;
                },
                (runnable, executor) -> {
                    throw new IllegalStateException("Thread pool executor is shutdown");
                });

        System.out.println("\nPrestart all core threads...\n");
        threadPoolExecutor.prestartAllCoreThreads();

        System.out.println("\nProcess tasks by core pool size...\n");
        threadPoolExecutor.execute(() -> sleep(threadPoolExecutor));
        threadPoolExecutor.execute(() -> sleep(threadPoolExecutor));
        threadPoolExecutor.execute(() -> sleep(threadPoolExecutor));
        threadPoolExecutor.execute(() -> sleep(threadPoolExecutor));

        Thread.sleep(100);
        System.out.println("\nCheck queue size: " + threadPoolExecutor.getQueue().size());

        System.out.println("\nExecute up to queue capacity...");
        threadPoolExecutor.execute(() -> sleep(threadPoolExecutor));
        threadPoolExecutor.execute(() -> sleep(threadPoolExecutor));
        threadPoolExecutor.execute(() -> sleep(threadPoolExecutor));
        threadPoolExecutor.execute(() -> sleep(threadPoolExecutor));

        System.out.println("\nCheck queue size: " + threadPoolExecutor.getQueue().size());

        System.out.println("\nProcess task by max core pool size...\n");
        threadPoolExecutor.execute(() -> sleep(threadPoolExecutor));
        Thread.sleep(100);

        System.out.println("\nCall threadPoolExecutor.shutdown()\n");
        threadPoolExecutor.shutdown();

        try {
            threadPoolExecutor.execute(() -> sleep(threadPoolExecutor));
        } catch (Exception e) {
            System.out.printf("Rise exception caused by execute after shutdown... %s\n", e);
        }

        System.out.printf("\nThread %s wait until threadPoolExecutor termination...\n\n", Thread.currentThread().getName());

        threadPoolExecutor.awaitTermination(5, TimeUnit.MINUTES);

        System.out.printf("\nPool size %s.\n", threadPoolExecutor.getPoolSize());
        System.out.printf("\nthreadPoolExecutor terminated.\n");
    }

    private static void sleep(ThreadPoolExecutor threadPoolExecutor) {
        try {
            System.out.printf("Start do some work by %s. Pool size %s.\n", Thread.currentThread().getName(), threadPoolExecutor.getPoolSize());
            Thread.sleep(10000);
            System.out.printf("End do some work by %s. Pool size %s.\n", Thread.currentThread().getName(), threadPoolExecutor.getPoolSize());
        } catch (InterruptedException e) {
        }
    }
}