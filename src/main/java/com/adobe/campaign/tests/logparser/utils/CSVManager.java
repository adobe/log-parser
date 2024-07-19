/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CSVManager {
    protected static Logger log = LogManager.getLogger();

    /**
     * This method fetches a map of the data stored in the given file
     *
     * Author : gandomi
     *
     * @param in_fileToLoad A file to fetch
     * @param in_key The header key to use as the key in the map
     * @return A map with the key being the LogDataKey and the values stored for that LogEntry
     * @throws FileNotFoundException if the file is absent
     * @throws IOException if the file cannot be read
     */
    public static Map<String, List<String>> fetchCSVToMapList(final String in_key,
            final File in_fileToLoad) throws FileNotFoundException, IOException {

        Map<String, List<String>> l_coverageHistory = new HashMap<>();

        if (!in_fileToLoad.exists() || (in_fileToLoad.length() == 0)) {
            log.error("File {} could not be found.", in_fileToLoad.getPath());
            return l_coverageHistory;
        }

        Reader in = new FileReader(in_fileToLoad);
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(in);

        Integer i = 0;
        for (CSVRecord record : records) {

            List<String> lt_valuesList = record.getParser().getHeaderNames().stream().map(record::get)
                    .collect(Collectors.toList());
            l_coverageHistory.put((in_key != null) ? record.get(in_key) : String.valueOf(i), lt_valuesList);
            i++;
        }
        return l_coverageHistory;
    }



}
