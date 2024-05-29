/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.HashMap;
import java.util.Map;

import com.adobe.campaign.tests.logparser.core.*;
import org.testng.annotations.Test;

public class StdLogEntryTests {

    @Test
    public void testSimplePut() {

        GenericEntry l_inputData = new GenericEntry();
        l_inputData.fetchValueMap().put("AAZ", "12");

        GenericEntry l_inputData2 = new GenericEntry();
        l_inputData2.put("AAZ", "12");

        assertThat(l_inputData, is(equalTo(l_inputData2)));
    }

    @Test
    public void testSimpleGet() {

        GenericEntry l_inputData2 = new GenericEntry();
        l_inputData2.put("AAZ", "12");

        assertThat(l_inputData2.get("AAZ"), is(equalTo("12")));
    }

    @Test
    public void testEqualsSimple() {
        StdLogEntry l_myEntry = new GenericEntry();

        StdLogEntry l_myEntry2 = new GenericEntry();

        assertThat("Both should be equal", l_myEntry, is(equalTo(l_myEntry)));
        assertThat("Both should be equal", l_myEntry, is(equalTo(l_myEntry2)));

        assertThat("One being null is not allowed", l_myEntry, not(equalTo(null)));

        assertThat("One bein null is not allowed", l_myEntry, not(equalTo(new LogData<>())));

        l_myEntry.setFrequence(null);
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));

        l_myEntry2.setFrequence(null);
        assertThat("Both should  be equal", l_myEntry, equalTo(l_myEntry2));
        l_myEntry.setFrequence(1);

        l_myEntry2.setFrequence(2);
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));
        l_myEntry2.setFrequence(1);

        l_myEntry.setParseDefinition(null);
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));

        l_myEntry2.setParseDefinition(null);
        assertThat("Both should  be equal", l_myEntry, equalTo(l_myEntry2));

        l_myEntry.setParseDefinition(new ParseDefinition("A"));
        l_myEntry2.setParseDefinition(new ParseDefinition("B"));
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));

        l_myEntry.setParseDefinition(new ParseDefinition("B"));

        l_myEntry.setValuesMap(null);
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));

        l_myEntry2.setValuesMap(null);
        assertThat("Both should  be equal", l_myEntry, equalTo(l_myEntry2));

        l_myEntry2.setValuesMap(new HashMap<String, Object>());
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));

        l_myEntry.setValuesMap(new HashMap<>());
        l_myEntry.put("A", "B");

        l_myEntry2.put("C", "D");

        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));

    }

    @Test
    public void testCopyConstructor() {
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
        l_inputData.fetchValueMap().put("AAZ", "12");
        l_inputData.fetchValueMap().put("ZZZ", "14");
        l_inputData.fetchValueMap().put("BAU", "13");
        l_inputData.fetchValueMap().put("DAT", "AA");

        GenericEntry l_newEntry = l_inputData.copy();

        assertThat("The new entry should be the same as the old one", l_inputData, equalTo(l_newEntry));

        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.fetchValueMap().put("AAZ", "12");
        l_inputData2.fetchValueMap().put("ZZZ", "34");
        l_inputData2.fetchValueMap().put("BAU", "14");
        l_inputData2.fetchValueMap().put("DAT", "DDD");

        GenericEntry l_newEntry2 = l_inputData2.copy();
        assertThat("The new entry should be the same as the old one", l_inputData2, equalTo(l_newEntry2));

        assertThat("The new entries should not be the same", l_newEntry2, not(equalTo(l_newEntry)));
        
        l_newEntry2.fetchValueMap().put("BAU", "15");
        
        assertThat("The new entry should no longer be the same as the old one", l_inputData2, equalTo(l_newEntry2));

        
    }

    @Test
    public void testMatches() {
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
        l_inputData.fetchValueMap().put("AAZ", "12");
        l_inputData.fetchValueMap().put("ZZZ", "14");
        l_inputData.fetchValueMap().put("BAU", "13");
        l_inputData.fetchValueMap().put("DAT", "AA");

        Map<String, Object> l_filterMap = new HashMap<>();
        l_filterMap.put("BAU", "13");
        assertThat("Matches should be true", l_inputData.matches(l_filterMap));

        Map<String, Object> l_filterMap2 = new HashMap<>();
        l_filterMap2.put("NOTBAU", "13");
        assertThat("We should not have a match if the header does not exist",
                !l_inputData.matches(l_filterMap2));

        Map<String, Object> l_filterMap3 = new HashMap<>();
        l_filterMap3.put("BAU", "16");
        assertThat("We should not have a match if the entry value is incorrect",
                !l_inputData.matches(l_filterMap3));

    }
}
