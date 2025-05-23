package com.async;

import com.github.domain.CsvReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public class ExecutorTest2 {

    /**
     * 月度分析（非常耗时）
     */
    public static Map<String, Long> monthAnalysis() throws URISyntaxException, IOException {
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
        // 异步执行任务
        // CompletableFuture.runAsync() // 在任务不需要有返回结果
        // CompletableFuture.supplyAsync() // 在任务需要返回结果

        log.info("开始统计");
        CompletableFuture<Map<String, Long>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return monthAnalysis();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        mapCompletableFuture.thenAccept((m)-> {
           m.forEach((k,v) -> log.info("key={},value={}",k,v));
        });

        log.info("执行其它操作...");

        System.in.read();

    }

}
