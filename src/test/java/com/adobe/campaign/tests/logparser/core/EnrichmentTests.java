/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import com.adobe.campaign.tests.logparser.data.SDKCase2;
import com.adobe.campaign.tests.logparser.data.SDKCaseSTD;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class EnrichmentTests {

    @Test
    public void testSimpleEnrichment() {
        LogData<GenericEntry> l_cubeData = fetchTestLogEntry();

        // Checks before enrichment
        assertThat(l_cubeData.getEntries().size(), Matchers.is(3));
        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence"));

        //// enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();
        l_queryMap.put("AAZ", Matchers.startsWith("12"));

        l_cubeData.enrichData(l_queryMap, "TIT", "TAT");

        LogData l_filteredLogs = l_cubeData.filterBy(Map.of("TIT", Matchers.equalTo("TAT")));
        assertThat("We should have enriched two entries", l_filteredLogs.getEntries().size(), Matchers.equalTo(2));
        List<ParseDefinitionEntry> l_definitionEntries = l_filteredLogs.get("12").getParseDefinition()
                .getDefinitionEntries();
        assertThat("We should have one additional Parse definition entry", l_definitionEntries.size(),
                Matchers.equalTo(generateTestParseDefinition().getDefinitionEntries().size() + 1));

        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("12").get("TIT"), Matchers.equalTo("TAT"));

        assertThat(l_cubeData.get("112").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("112").get("TIT"), Matchers.equalTo(""));

        l_cubeData.exportLogDataToHTML("dsd", "enriched.html");
    }

    @Test
    public void testDoubleEnrichment() {
        LogData<GenericEntry> l_cubeData = fetchTestLogEntry();

        // Checks before enrichment
        assertThat(l_cubeData.getEntries().size(), Matchers.is(3));
        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence"));

        //// enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();
        l_queryMap.put("AAZ", Matchers.startsWith("12"));

        l_cubeData.enrichData(l_queryMap, "TIT", "TAT");

        Map<String, Matcher> l_queryMap2 = new HashMap<>();
        l_queryMap2.put("TIT", Matchers.equalTo(""));

        l_cubeData.enrichData(l_queryMap2, "TIT", "TUT");

        LogData l_filteredLogs = l_cubeData.filterBy(Map.of("TIT", Matchers.equalTo("TAT")));
        assertThat("We should have enriched two entries", l_filteredLogs.getEntries().size(), Matchers.equalTo(2));
        List<ParseDefinitionEntry> l_definitionEntries = l_filteredLogs.get("12").getParseDefinition()
                .getDefinitionEntries();
        assertThat("We should have one additional Parse definition entry", l_definitionEntries.size(),
                Matchers.equalTo(generateTestParseDefinition().getDefinitionEntries().size() + 1));

        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("12").get("TIT"), Matchers.equalTo("TAT"));

        assertThat(l_cubeData.get("112").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("112").get("TIT"), Matchers.equalTo("TUT"));

        l_cubeData.exportLogDataToHTML("dsd", "enriched.html");
    }

    @Test
    public void testDoubleEnrichment_updateUnset() {
        LogData<GenericEntry> l_cubeData = fetchTestLogEntry();

        // Checks before enrichment
        assertThat(l_cubeData.getEntries().size(), Matchers.is(3));
        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence"));

        //// enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();
        l_queryMap.put("AAZ", Matchers.startsWith("12"));

        l_cubeData.enrichData(l_queryMap, "TIT", "TAT");

        Map<String, Matcher> l_queryMap2 = new HashMap<>();
        l_queryMap2.put("TIT", Matchers.equalTo(""));

        l_cubeData.enrichEmpty("TIT", "TUT");

        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("12").get("TIT"), Matchers.equalTo("TAT"));

        assertThat(l_cubeData.get("112").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("112").get("TIT"), Matchers.equalTo("TUT"));

        l_cubeData.exportLogDataToHTML("dsd", "enriched.html");
    }

    @Test
    public void testDoubleEnrichment_SDK() throws StringParseException {

        ParseDefinition l_pDefinition = SDKTests.getTestParseDefinition();
        l_pDefinition.setStoreFileName(true);

        String l_file = "src/test/resources/sdk/useCase1.log";

        LogData<SDKCaseSTD> l_entries = LogDataFactory.generateLogData(Arrays.asList(l_file), l_pDefinition,
                SDKCaseSTD.class);

        Map<String, Matcher> l_queryMap = Map.of("code", Matchers.startsWith("INT"));
        l_entries.enrichData(l_queryMap, "category", "GREAT");

        Map<String, Matcher> l_queryMap2 = Map.of("code", Matchers.startsWith("SOP"));
        l_entries.enrichData(l_queryMap2, "category", "AVERAGE");

        assertThat("We should have the correct value", l_entries.get("INT-150612").get("category"),
                is(equalTo("GREAT")));
        assertThat("We should have the correct value", l_entries.get("SOP-338921").get("category"),
                is(equalTo("AVERAGE")));
    }

    @Test
    public void testDoubleEnrichment_SDK_timeBased() throws StringParseException {

        ParseDefinition l_pDefinition = SDKTests.getTestParseDefinition();
        l_pDefinition.setStoreFileName(true);

        String l_file = "src/test/resources/sdk/useCase1.log";

        LogData<SDKCase2> l_entries = LogDataFactory.generateLogData(Arrays.asList(l_file), l_pDefinition,
                SDKCase2.class);

        l_entries.exportLogDataToHTML("", "time");

        ZonedDateTime start = ZonedDateTime.parse("2024-06-13T03:00:10.727Z", DateTimeFormatter.ISO_ZONED_DATE_TIME);
        ZonedDateTime end = ZonedDateTime.parse("2024-06-13T11:00:19.727Z", DateTimeFormatter.ISO_ZONED_DATE_TIME);

        Map<String, Matcher> l_queryMap = Map.of("timeOfLog",
                Matchers.allOf(Matchers.greaterThanOrEqualTo(start), Matchers.lessThanOrEqualTo(end)));
        l_entries.enrichData(l_queryMap, "test", "Test1");

        assertThat("We should have the correct value", l_entries.get("INT-150612").get("test"), is(equalTo("Test1")));
        assertThat("We should have the correct value", l_entries.get("SOP-338921").get("test"), is(equalTo("")));
        assertThat("We should have the correct value", l_entries.get("INT-158912").get("test"), is(equalTo("")));
        assertThat("We should have the correct value", l_entries.get("WEB-530007").get("test"), is(equalTo("Test1")));
    }

    @Test
    public void testSimpleEnrichmentWithMap() {
        LogData<GenericEntry> l_cubeData = fetchTestLogEntry();

        // Checks before enrichment
        assertThat(l_cubeData.getEntries().size(), Matchers.is(3));
        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence"));

        //// enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();
        l_queryMap.put("AAZ", Matchers.startsWith("12"));

        Map<String, String> keyValueToEnrich = new HashMap<>();
        keyValueToEnrich.put("TIT", "TAT");

        l_cubeData.enrichData(l_queryMap, keyValueToEnrich);

        LogData l_filteredLogs = l_cubeData.filterBy(Map.of("TIT", Matchers.equalTo("TAT")));
        assertThat("We should have enriched two entries", l_filteredLogs.getEntries().size(), Matchers.equalTo(2));
        List<ParseDefinitionEntry> l_definitionEntries = l_filteredLogs.get("12").getParseDefinition()
                .getDefinitionEntries();
        assertThat("We should have one additional Parse definition entry", l_definitionEntries.size(),
                Matchers.equalTo(generateTestParseDefinition().getDefinitionEntries().size() + 1));

        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("12").get("TIT"), Matchers.equalTo("TAT"));

        assertThat(l_cubeData.get("112").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("112").get("TIT"), Matchers.equalTo(""));

        l_cubeData.exportLogDataToHTML("dsd", "enriched.html");
    }

    @Test
    public void testSimpleEnrichmentWithEmptyMap() {
        LogData<GenericEntry> l_cubeData = fetchTestLogEntry();

        // Checks before enrichment
        assertThat(l_cubeData.getEntries().size(), Matchers.is(3));
        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence"));

        //// enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();
        l_queryMap.put("AAZ", Matchers.startsWith("12"));

        Map<String, String> keyValueToEnrich = new HashMap<>();
        l_cubeData.enrichData(l_queryMap, keyValueToEnrich);

        LogData l_filteredLogs = l_cubeData.filterBy(Map.of("AAZ", Matchers.equalTo("12")));
        List<ParseDefinitionEntry> l_definitionEntries = l_filteredLogs.get("12").getParseDefinition()
                .getDefinitionEntries();
        assertThat("We should have one additional Parse definition entry", l_definitionEntries.size(),
                Matchers.equalTo(generateTestParseDefinition().getDefinitionEntries().size()));
        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence"));
        assertThat(l_cubeData.get("112").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence"));

        l_cubeData.exportLogDataToHTML("dsd", "enriched.html");
    }

    @Test
    public void testDoubleEnrichmentWithMap() {
        LogData<GenericEntry> l_cubeData = fetchTestLogEntry();

        // Checks before enrichment
        assertThat(l_cubeData.getEntries().size(), Matchers.is(3));
        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence"));

        //// enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();
        l_queryMap.put("AAZ", Matchers.startsWith("12"));

        Map<String, String> keyValueToEnrich = new HashMap<>();
        keyValueToEnrich.put("TIT", "TAT");
        keyValueToEnrich.put("TOT", "TET");

        l_cubeData.enrichData(l_queryMap, keyValueToEnrich);

        Map<String, Matcher> l_queryMap2 = new HashMap<>();
        l_queryMap2.put("TIT", Matchers.equalTo(""));

        Map<String, String> otherKeyValueToEnrich = new HashMap<>();
        otherKeyValueToEnrich.put("TIT", "TUT");
        otherKeyValueToEnrich.put("TOT", "TUTU");

        l_cubeData.enrichData(l_queryMap2, otherKeyValueToEnrich);

        LogData l_filteredLogs = l_cubeData.filterBy(Map.of("TIT", Matchers.equalTo("TAT")));
        assertThat("We should have enriched two entries", l_filteredLogs.getEntries().size(), Matchers.equalTo(2));
        LogData l_secondFilteredLogs = l_cubeData.filterBy(Map.of("TOT", Matchers.equalTo("TET")));
        assertThat("We should have enriched two entries", l_secondFilteredLogs.getEntries().size(),
                Matchers.equalTo(2));
        List<ParseDefinitionEntry> l_definitionEntries = l_filteredLogs.get("12").getParseDefinition()
                .getDefinitionEntries();
        assertThat("We should have one additional Parse definition entry", l_definitionEntries.size(),
                Matchers.equalTo(generateTestParseDefinition().getDefinitionEntries().size() + 2));

        assertThat(l_cubeData.get("12").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT", "TOT"));
        assertThat(l_cubeData.get("12").get("TIT"), Matchers.equalTo("TAT"));

        assertThat(l_cubeData.get("112").fetchStoredHeaders(),
                Matchers.containsInAnyOrder("key", "AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT", "TOT"));
        assertThat(l_cubeData.get("112").get("TIT"), Matchers.equalTo("TUT"));
        assertThat(l_cubeData.get("112").get("TOT"), Matchers.equalTo("TUTU"));

        l_cubeData.exportLogDataToHTML("dsd", "enriched.html");
    }

    @Test
    public void testDoubleEnrichment_SDK_WithMap() throws StringParseException {

        ParseDefinition l_pDefinition = SDKTests.getTestParseDefinition();
        l_pDefinition.setStoreFileName(true);

        String l_file = "src/test/resources/sdk/useCase1.log";

        LogData<SDKCaseSTD> l_entries = LogDataFactory.generateLogData(Arrays.asList(l_file), l_pDefinition,
                SDKCaseSTD.class);

        Map<String, Matcher> l_queryMap = Map.of("code", Matchers.startsWith("INT"));
        Map<String, String> keyValueToEnrich = new HashMap<>();
        keyValueToEnrich.put("category", "GREAT");

        l_entries.enrichData(l_queryMap, keyValueToEnrich);

        Map<String, String> otherKeyValueToEnrich = new HashMap<>();
        keyValueToEnrich.put("category", "AVERAGE");

        Map<String, Matcher> l_queryMap2 = Map.of("code", Matchers.startsWith("SOP"));
        l_entries.enrichData(l_queryMap2, keyValueToEnrich);

        assertThat("We should have the correct value", l_entries.get("INT-150612").get("category"),
                is(equalTo("GREAT")));
        assertThat("We should have the correct value", l_entries.get("SOP-338921").get("category"),
                is(equalTo("AVERAGE")));
    }

    private static LogData<GenericEntry> fetchTestLogEntry() {
        ParseDefinition l_definition = generateTestParseDefinition();

        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.getValuesMap().put("AAZ", "12");
        l_inputData.getValuesMap().put("ZZZ", "14");
        l_inputData.getValuesMap().put("BAU", "13");
        l_inputData.getValuesMap().put("DAT", "AA");

        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.getValuesMap().put("AAZ", "112");
        l_inputData2.getValuesMap().put("ZZZ", "114");
        l_inputData2.getValuesMap().put("BAU", "113");
        l_inputData2.getValuesMap().put("DAT", "AAA");

        GenericEntry l_inputData3 = new GenericEntry(l_definition);
        l_inputData3.getValuesMap().put("AAZ", "120");
        l_inputData3.getValuesMap().put("ZZZ", "14");
        l_inputData3.getValuesMap().put("BAU", "13");
        l_inputData3.getValuesMap().put("DAT", "AAA");

        LogData<GenericEntry> l_cubeData = new LogData<GenericEntry>();
        l_cubeData.addEntry(l_inputData);
        l_cubeData.addEntry(l_inputData2);
        l_cubeData.addEntry(l_inputData3);
        return l_cubeData;
    }

    private static ParseDefinition generateTestParseDefinition() {
        ParseDefinition l_definition = new ParseDefinition("tmp");

        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.addEntry(new ParseDefinitionEntry("ZZZ"));
        final ParseDefinitionEntry l_testParseDefinitionEntryBAU = new ParseDefinitionEntry("BAU");
        l_definition.addEntry(l_testParseDefinitionEntryBAU);
        final ParseDefinitionEntry l_testParseDefinitionEntryDAT = new ParseDefinitionEntry("DAT");
        l_definition.addEntry(l_testParseDefinitionEntryDAT);
        l_definition.defineKeys(l_parseDefinitionEntryKey);
        return l_definition;
    }
}
