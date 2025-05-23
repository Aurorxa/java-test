package com.github;

import com.github.domain.CsvReader;
import com.github.domain.CsvRecord;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
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
                    return String.format("%d-%02d", eventTime.getYear(), eventTime.getMonthValue());
                },
                TreeMap::new,
                Collectors.counting()));

        map.forEach((key, value) -> System.out.println(key +" "+ value));
    }


}
