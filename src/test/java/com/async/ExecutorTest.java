package com.async;

import com.github.domain.CsvReader;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class ExecutorTest {

    /**
     * 月度分析（非常耗时）
     */
    public static Map<String, Long> monthAnalysis() throws Exception {
        Map<String, Long> map = CsvReader
                .readCsvFile()
                .collect(Collectors.groupingBy(
                        (csv) -> {
                            LocalDateTime eventTime = csv.getEventTime();
                            return YearMonth
                                    .from(eventTime)
                                    .toString();
                        },
                        TreeMap::new,
                        Collectors.counting()));

        log.info("{}", map.size());
        return map;
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = null;
        try {
            executorService = Executors.newFixedThreadPool(3);
            log.info("开始统计");
            final Future<Map<String, Long>> submitFuture1 = executorService.submit(() -> {
                try {
                    return monthAnalysis();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            final Future<Map<String, Long>> submitFuture2 = executorService.submit(() -> {
                try {
                    return monthAnalysis();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            log.info("执行其它操作...");
            try {
                // 阻塞等待任务完成
                final Map<String, Long> map1 = submitFuture1.get();
                log.info("{}", map1.size());
            } catch (ExecutionException e) {
                System.err.println("任务执行异常: " + e
                        .getCause()
                        .getMessage());
            }
            try {
                // 阻塞等待任务完成
                final Map<String, Long> map2 = submitFuture2.get();
                log.info("{}", map2.size());
            } catch (ExecutionException e) {
                System.err.println("任务执行异常: " + e
                        .getCause()
                        .getMessage());
            }
            log.info("执行其它操作2...");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != executorService) {
                executorService.shutdown();
            }
            try {
                if (!Objects
                        .requireNonNull(executorService)
                        .awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow(); // 超时后强制关闭
                }
            } catch (InterruptedException ie) {
                Objects
                        .requireNonNull(executorService)
                        .shutdownNow(); // 捕获中断异常后强制关闭
                Thread
                        .currentThread()
                        .interrupt(); // 重新设置中断标志
            }
        }
    }

}
