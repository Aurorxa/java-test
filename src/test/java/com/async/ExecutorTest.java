package com.async;

import com.github.domain.CsvReader;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class ExecutorTest {

    /**
     * 月度分析（非常耗时）
     */
    public static void monthAnalysis(Consumer<Map<String, Long>> consumer) throws Exception {
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

        consumer.accept(map);
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = null;
        try {
            executorService = Executors.newFixedThreadPool(3);
            log.info("开始统计");
            executorService.submit(() -> {
                try {
                    monthAnalysis(map -> {
                        log.info("{}", map.size());
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            executorService.submit(() -> {
                try {
                    monthAnalysis(map -> {
                        log.info("{}", map.size());
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            log.info("执行其它操作...");
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
