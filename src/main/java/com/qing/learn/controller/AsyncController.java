package com.qing.learn.controller;

import com.qing.learn.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/async")
    public String async() {
        asyncService.asyncMethod();
        System.out.println("ok" + Thread.currentThread().getName());
        return "Async method called";
    }

    @GetMapping("/async2")
    public String async2() {
        CompletableFuture<String> future = asyncService.asyncMethodWithResult();

        String result;
        try {
            // 阻塞，直到异步任务完成并返回结果
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result = "Exception occurred!";
        }

        return "Async method result: " + result;
    }

    /**
     * 如果有多个异步任务，并且需要在所有任务完成后进行下一步处理，可以使用`CompletableFuture`的组合方法，如`allOf`或`anyOf`。
     */
    @GetMapping("/async3")
    public String async3() {
        CompletableFuture<String> future1 = asyncService.asyncMethod1();
        CompletableFuture<String> future2 = asyncService.asyncMethod2();

        String result;
        try {
            // 使用 allOf 等待所有异步任务完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2);
            allFutures.get(); // 阻塞，直到所有任务完成

            // 获取每个任务的结果
            String result1 = future1.get();
            String result2 = future2.get();

            result = "Results: " + result1 + ", " + result2;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result = "Exception occurred!";
        }

        return result;
    }
}