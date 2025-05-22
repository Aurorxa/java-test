package com.github;

import com.github.domain.CsvReader;
import com.github.domain.CsvRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

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
        // 准备一个测试文件 data.csv
        File file = new File("src/test/resources/data.csv");

        List<CsvRecord> records = csvReader.readCsvFile(file);

        System.out.println("records = " + records);

    }

}
