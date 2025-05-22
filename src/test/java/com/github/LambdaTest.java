package com.github;

import com.github.domain.CsvReader;
import com.github.domain.CsvRecord;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author 许大仙
 * @version 1.0
 * @since 2025-05-22 16:23
 */
public class LambdaTest {

    private CsvReader csvReader;

    @Before
    public void setUp() throws Exception {
        // 初始化 CsvReader 对象
        csvReader = new CsvReader();
    }

    @Test
    public void testReadCsvFile() throws Exception {


        long count = 0;
        try (Stream<CsvRecord> csvRecordStream = csvReader.readCsvFile(Path
                .of(
                        LambdaTest.class
                                .getClassLoader()
                                .getResource("data.csv")
                                .toURI())
                .toFile())) {

            count = csvRecordStream.count();
        }

        System.out.println("count = " + count);

    }

}
