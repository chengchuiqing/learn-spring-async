package com.qing.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 注意：
 * 在使用spring的异步多线程时经常回碰到多线程失效的问题，解决方式为：
 * 异步方法和调用方法一定要写在不同的类中 ,如果写在一个类中,是没有效果的！
 *
 * 原因：
 * spring对@Transactional注解时也有类似问题，spring扫描时具有@Transactional注解方法的类时，是生成一个代理类，由代理类去开启关闭事务，而在同一个类中，方法调用是在类体内执行的，spring无法截获这个方法调用。
 */
@EnableAsync
@SpringBootApplication
public class LearnAsyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnAsyncApplication.class, args);
    }

}
