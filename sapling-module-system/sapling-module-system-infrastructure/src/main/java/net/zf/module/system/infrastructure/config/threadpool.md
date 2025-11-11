如果要新增一个线程池，只需要：

1. 在配置文件中添加新的线程池配置（参数按照业务调整）：
```yaml
thread-pool:
  executors:
    # 新增的线程池配置
    new-executor:
      core-pool-size: 16
      max-pool-size: 32
      keep-alive-time: 10
      queue-capacity: 32
      thread-name-prefix: New-Executor-
      allow-core-thread-time-out: true
      wait-for-tasks-to-complete-on-shutdown: true
      await-termination-seconds: 60
      rejected-execution-handler: CALLER_RUNS
```

2. 在 `ThreadPoolConfig` 中添加新的 Bean 方法：
```java
@Bean("newExecutor")
public ThreadPoolTaskExecutor newExecutor() {
    return createThreadPoolTaskExecutor("new-executor", "New-Executor-");
}
```
 
使用示例：
```java
@Service
public class YourService {
    @Resource(name = "newExecutor")
    private ThreadPoolTaskExecutor newExecutor;

    public void yourMethod() {
        CompletableFuture.supplyAsync(() -> {
            // 你的业务逻辑
            return result;
        }, newExecutor);
    }
}
```

