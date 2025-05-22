package com.github.domain;


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CsvReader {

    public Stream<CsvRecord> readCsvFile(File csvFile) throws Exception {
        final CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.SKIP_EMPTY_LINES);
        csvMapper.disable(CsvParser.Feature.TRIM_SPACES);
        // CsvSchema schema = csvMapper.schemaFor(CsvRecord.class).withHeader();

        CsvSchema schema = CsvSchema.builder()
                .addColumn("id")
                .addColumn("event_time")   // ✅ 对应 @JsonProperty("event_time")
                .addColumn("order_id")
                .addColumn("product_id")
                .addColumn("category_id")
                .addColumn("category_code")
                .addColumn("brand")
                .addColumn("price")
                .addColumn("user_id")
                .addColumn("age")
                .addColumn("sex")
                .addColumn("local")
                .build()
                .withHeader(); // 如果有标题行就启用
        Reader reader = new FileReader(csvFile);
        MappingIterator<CsvRecord> iterator =
                csvMapper.readerFor(CsvRecord.class).with(schema).readValues(reader);

        // 将 MappingIterator 转换为 Stream
        Spliterator<CsvRecord> spliterator =
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);

        return StreamSupport
                .stream(spliterator, false)
                .onClose(() -> {
                    try {
                        reader.close();
                    } catch (Exception ignored) {}
                });
    }
}