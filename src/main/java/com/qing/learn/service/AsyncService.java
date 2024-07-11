package com.qing.learn.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncService {

    @Async("taskExecutor")
    public void asyncMethod() {
        System.out.println("Execute method asynchronously - " + Thread.currentThread().getName());
        // 模拟长时间运行的任务
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Async method finished - " + Thread.currentThread().getName());
    }

    // ================================================================================================================

    @Async("taskExecutor")
    public CompletableFuture<String> asyncMethodWithResult() {
        System.out.println("Execute method asynchronously - " + Thread.currentThread().getName());
        // 模拟长时间运行的任务
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = "Async result";
        System.out.println("Async method finished - " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture(result);
    }

    // =================================================================================================================

    @Async("taskExecutor")
    public CompletableFuture<String> asyncMethod1() {
        // 模拟长时间运行的任务
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture("Result from asyncMethod1");
    }

    @Async("taskExecutor")
    public CompletableFuture<String> asyncMethod2() {
        // 模拟长时间运行的任务
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture("Result from asyncMethod2");
    }

    // ================================================================================================================

    /**
     * 在异步任务中处理可能出现的异常，并返回适当的结果
     * @return
     */
    @Async("taskExecutor")
    public CompletableFuture<String> asyncMethodWithExceptionHandling() {
        try {
            // 模拟长时间运行的任务
            Thread.sleep(5000);
            // 模拟异常
            if (true) throw new RuntimeException("Simulated exception");
            return CompletableFuture.completedFuture("Async result");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Interrupted exception occurred!");
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Exception occurred!");
        }
    }
}