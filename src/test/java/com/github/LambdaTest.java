package com.github;

import com.github.domain.CsvReader;
import com.github.domain.CsvRecord;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LambdaTest {

    private Stream<CsvRecord> stream;

    @Before
    public void before() throws Exception {
        stream = CsvReader.readCsvFile();
    }

    @Test
    public void testCount() {
        long count = stream.count();
        System.out.println("count = " + count);
    }

    @Test
    public void testAnalyzeOrderByMonth() {
        Map<String, Long> map = stream.collect(Collectors.groupingBy(
                (csv) -> {
                    LocalDateTime eventTime = csv.getEventTime();
                    return YearMonth
                            .from(eventTime)
                            .toString();
                },
                TreeMap::new,
                Collectors.counting()));

        map.forEach((key, value) -> System.out.println(key + " 的订单数是：" + value));
    }

    @Test
    public void testAnalyzeMaxOrderByMonth() {
        // 先根据月份分组统计，获取 月份 -- 个数
        Map<String, Long> map = stream.collect(Collectors.groupingBy(
                (csv) -> {
                    LocalDateTime eventTime = csv.getEventTime();
                    return YearMonth
                            .from(eventTime)
                            .toString();
                },
                Collectors.counting()));

        // 对 Map 进行排序，获取最大值
        Optional<Map.Entry<String, Long>> optional = map
                .entrySet()
                .stream()
                // .max(Comparator.comparingLong(Map.Entry::getValue));
                .max(Map.Entry.comparingByValue());

        optional.ifPresent(me -> System.out.println(me.getKey() + " 的订单数是：" + me.getValue()));
    }

    @Test
    public void testAnalyzeMaxOrderByProduct() {
        // 先根据 productId 分组统计，获取 productId -- 个数
        Map<String, Long> map = stream.collect(Collectors.groupingBy(
                CsvRecord::getProductId,
                Collectors.counting()));

        // 对 Map 进行排序，获取最大值
        Optional<Map.Entry<String, Long>> optional = map
                .entrySet()
                .stream()
                // .max(Comparator.comparingLong(Map.Entry::getValue));
                .max(Map.Entry.comparingByValue());

        optional.ifPresent(me -> System.out.println(me.getKey() + " 的订单数是：" + me.getValue()));
    }

    @Test
    public void testAnalyzeMaxOrderByUserTop10() {
        // 先根据 userId 分组统计，获取 userId -- 个数
        Map<String, Long> map = stream.collect(Collectors.groupingBy(
                CsvRecord::getUserId,
                Collectors.counting()));

        // 对 使用 LinkedHashMap 收集，会自动保持排序
        Map<String, Long> me = map
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));

        me.forEach((key, value) -> System.out.println(key + " 的订单数是：" + value));
    }

    @Test
    public void testAnalyzeMaxOrderByArea() {
        // 根据地区分组，再根据用户分组，获取每个地区每个用户的订单数
        Map<String, Map<String, Long>> map = stream.collect(Collectors.groupingBy(
                CsvRecord::getLocal,
                Collectors.groupingBy(CsvRecord::getUserId, Collectors.counting())));

        // 对每个地区用户的订单数求最大值
        Map<String, Optional<Map.Entry<String, Long>>> resultMap = map
                .entrySet()
                .stream()
                .map(
                        e -> Map.entry(e.getKey(),
                                e
                                        .getValue()
                                        .entrySet()
                                        .stream()
                                        .max(Map.Entry.comparingByValue()))
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 遍历 Map
        resultMap.forEach((area, entry) -> {
            Map.Entry<String, Long> m = entry.orElse(new AbstractMap.SimpleEntry<>("", 0L));
            System.out.printf("地区：%s，下单最多的用户id：%s，订单数是：%s \n", area, m.getKey(), m.getValue());
        });
    }

    @Test
    public void testAnalyzeMaxOrderByAreaTop3() {
        // 根据地区分组，再根据用户分组，获取每个地区每个用户的订单数
        Map<String, Map<String, Long>> map = stream.collect(Collectors.groupingBy(
                CsvRecord::getLocal,
                Collectors.groupingBy(CsvRecord::getUserId, Collectors.counting())));

        // 对每个地区用户的订单数求前 3 名
        Map<String, List<Map.Entry<String, Long>>> resultMap = map
                .entrySet()
                .stream()
                .map(
                        e -> Map.entry(e.getKey(),
                                e
                                        .getValue()
                                        .entrySet()
                                        .stream()
                                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                        .limit(3)
                                        .collect(Collectors.toList()))
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 遍历 Map
        resultMap.forEach((area, list) -> {
            System.out.println("地区：" + area);
            System.out.println("下单最多的前 3 名信息：");
            list.forEach((e) -> {
                final String key = e.getKey();
                final Long value = e.getValue();
                System.out.println("用户id：" + key + "，订单数：" + value);
            });

            System.out.println("--------------------------------------------");
        });
    }

    @Test
    public void testAnalyzeOrderByCategoryCode() {
        Map<String, Long> map = stream
                .filter(csv -> !csv
                        .getCategoryCode()
                        .isEmpty())
                .collect(Collectors.groupingBy(
                        (csv)-> {
                            final String categoryCode = csv
                                    .getCategoryCode();
                            final int index = categoryCode
                                    .indexOf(".");
                            return categoryCode.substring(0, index);
                        },
                        TreeMap::new,
                        Collectors.counting()));

        map.forEach((key, value) -> System.out.println(key + " 的订单数是：" + value));
    }

    @Test
    public void testAnalyzeInterval() {

        Map<String, Long> map = stream
                .filter(csv->csv.getPrice() > 0)
                .collect(Collectors.groupingBy(
                        (csv)->getPriceRangeLabel(csv.getPrice()),
                        TreeMap::new,
                        Collectors.counting()));

        map.forEach((key, value) -> System.out.println(key + " 的订单数是：" + value));
    }

    private static String getPriceRangeLabel(double price) {
        if (price < 100) return "[0,100)";
        else if (price < 500) return "[100,500)";
        else if (price < 1000) return "[500,1000)";
        else return "[1000,+∞)";
    }

}
