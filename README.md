## 一 基本使用
在Spring Boot中，可以通过注解的方式配置和使用异步线程池，以提高应用的并发处理能力。以下是详细步骤：

### 1. 添加依赖

确保在你的`pom.xml`文件中添加了必要的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 2. 启用异步支持

在你的主启动类上添加`@EnableAsync`注解：

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. 配置异步线程池

创建一个配置类，配置异步线程池：

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
}
```

### 4. 使用异步方法

在需要异步执行的方法上添加`@Async`注解。你可以指定使用的线程池名称，如果不指定，则使用默认线程池：

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
}
```

### 5. 调用异步方法

在你的控制器或其他服务中调用异步方法：

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/async")
    public String async() {
        asyncService.asyncMethod();
        return "Async method called";
    }
}
```

### 6. 配置日志输出（可选）

为了更好地观察异步方法的执行情况，可以配置日志输出：

```yaml
logging:
  level:
    org.springframework: DEBUG
```

完成上述步骤后，你的Spring Boot应用程序就可以在异步线程池中执行任务了。通过调整线程池配置，你可以优化异步任务的执行性能。




## 二、获取异步任务结果
如果你需要在异步任务中获取结果值，并在任务完成后进行后续业务逻辑处理，可以使用`Future`或者`CompletableFuture`。以下是详细步骤和示例：

### 1. 修改异步方法返回值类型

修改异步方法的返回值类型为`CompletableFuture<T>`，以便异步任务能够返回结果值。

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncService {

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
}
```

### 2. 调用异步方法并获取结果

在控制器或其他服务中调用异步方法，并使用`CompletableFuture`获取结果。

```java
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
}
```

### 3. 使用`CompletableFuture`的组合方法（可选）

如果你有多个异步任务，并且需要在所有任务完成后进行下一步处理，可以使用`CompletableFuture`的组合方法，如`allOf`或`anyOf`。

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncService {

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
}
```

```java
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
```

### 4. 异常处理

在异步任务中处理可能出现的异常，并返回适当的结果：

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncService {

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
```

通过这些步骤，你可以在Spring Boot中使用异步方法获取结果，并在任务完成后进行后续业务逻辑处理。