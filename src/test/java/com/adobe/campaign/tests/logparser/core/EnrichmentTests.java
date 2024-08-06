/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import com.adobe.campaign.tests.logparser.data.SDKCaseSTD;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertThrows;

public class EnrichmentTests {

    @Test
    public void testSimpleEnrichment() {
        LogData<GenericEntry> l_cubeData = fetchTestLogEntry();

        //Checks before enrichment
        assertThat(l_cubeData.getEntries().size(), Matchers.is(3));
        assertThat(l_cubeData.get("12").fetchStoredHeaders(), Matchers.containsInAnyOrder("key","AAZ", "ZZZ", "BAU", "DAT", "frequence"));


        ////enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();
        l_queryMap.put("AAZ", Matchers.startsWith("12"));

        l_cubeData.enrichData(l_queryMap, "TIT", "TAT");

        assertThat(l_cubeData.get("12").fetchStoredHeaders(), Matchers.containsInAnyOrder("key","AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("12").get("TIT"), Matchers.equalTo("TAT"));

        assertThat(l_cubeData.get("112").fetchStoredHeaders(), Matchers.containsInAnyOrder("key","AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("112").get("TIT"), Matchers.equalTo(""));

        l_cubeData.exportLogDataToHTML("dsd", "enriched");
    }

    @Test
    public void testDoubleEnrichment() {
        LogData<GenericEntry> l_cubeData = fetchTestLogEntry();

        //Checks before enrichment
        assertThat(l_cubeData.getEntries().size(), Matchers.is(3));
        assertThat(l_cubeData.get("12").fetchStoredHeaders(), Matchers.containsInAnyOrder("key","AAZ", "ZZZ", "BAU", "DAT", "frequence"));

        ////enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();
        l_queryMap.put("AAZ", Matchers.startsWith("12"));

        l_cubeData.enrichData(l_queryMap, "TIT", "TAT");

        Map<String, Matcher> l_queryMap2 = new HashMap<>();
        l_queryMap2.put("TIT", Matchers.equalTo(""));

        l_cubeData.enrichData(l_queryMap2, "TIT", "TUT");

        assertThat(l_cubeData.get("12").fetchStoredHeaders(), Matchers.containsInAnyOrder("key","AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("12").get("TIT"), Matchers.equalTo("TAT"));

        assertThat(l_cubeData.get("112").fetchStoredHeaders(), Matchers.containsInAnyOrder("key","AAZ", "ZZZ", "BAU", "DAT", "frequence", "TIT"));
        assertThat(l_cubeData.get("112").get("TIT"), Matchers.equalTo("TUT"));

        l_cubeData.exportLogDataToHTML("dsd", "enriched");
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


        assertThat("We should have the correct value", l_entries.get("INT-150612").get("category"), is(equalTo("GREAT")));
        assertThat("We should have the correct value", l_entries.get("SOP-338921").get("category"), is(equalTo("AVERAGE")));
        //assertThat("We should have the correct value", l_entries.get("SOP-338921").get("category"), is(equalTo("AVERAGE")));

    }

    private static LogData<GenericEntry> fetchTestLogEntry() {
        ParseDefinition l_definition = new ParseDefinition("tmp");

        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.addEntry(new ParseDefinitionEntry("ZZZ"));
        final ParseDefinitionEntry l_testParseDefinitionEntryBAU = new ParseDefinitionEntry("BAU");
        l_definition.addEntry(l_testParseDefinitionEntryBAU);
        final ParseDefinitionEntry l_testParseDefinitionEntryDAT = new ParseDefinitionEntry("DAT");
        l_definition.addEntry(l_testParseDefinitionEntryDAT);
        l_definition.defineKeys(l_parseDefinitionEntryKey);

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
}
