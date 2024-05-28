/*
 * MIT License
 *
 * © Copyright 2020 Adobe. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
     * <p>
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

        Map<String, List<String>> l_coverageHistory = new HashMap<String, List<String>>();

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
