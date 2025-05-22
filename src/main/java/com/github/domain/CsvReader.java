package com.github.domain;


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.util.List;

public class CsvReader {

    public List<CsvRecord> readCsvFile(File csvFile) throws Exception {
        final CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(CsvRecord.class).withHeader();
        MappingIterator<CsvRecord> csvRecordMappingIterator = new CsvMapper().readerWithTypedSchemaFor(CsvRecord.class).with(schema).readValues(csvFile);
        return csvRecordMappingIterator.readAll();
    }
}