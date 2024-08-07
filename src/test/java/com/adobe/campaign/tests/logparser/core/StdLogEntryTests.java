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
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class StdLogEntryTests {

    @Test
    public void testSimplePut() {

        GenericEntry l_inputData = new GenericEntry();
        l_inputData.getValuesMap().put("AAZ", "12");

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
        l_inputData.getValuesMap().put("AAZ", "12");
        l_inputData.getValuesMap().put("ZZZ", "14");
        l_inputData.getValuesMap().put("BAU", "13");
        l_inputData.getValuesMap().put("DAT", "AA");

        GenericEntry l_newEntry = l_inputData.copy();

        assertThat("The new entry should be the same as the old one", l_inputData, equalTo(l_newEntry));

        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.getValuesMap().put("AAZ", "12");
        l_inputData2.getValuesMap().put("ZZZ", "34");
        l_inputData2.getValuesMap().put("BAU", "14");
        l_inputData2.getValuesMap().put("DAT", "DDD");

        GenericEntry l_newEntry2 = l_inputData2.copy();
        assertThat("The new entry should be the same as the old one", l_inputData2, equalTo(l_newEntry2));

        assertThat("The new entries should not be the same", l_newEntry2, not(equalTo(l_newEntry)));
        
        l_newEntry2.getValuesMap().put("BAU", "15");
        
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
        l_inputData.getValuesMap().put("AAZ", "12");
        l_inputData.getValuesMap().put("ZZZ", "14");
        l_inputData.getValuesMap().put("BAU", "13");
        l_inputData.getValuesMap().put("DAT", "AA");

        Map<String, Matcher> l_filterMap = new HashMap<>();
        l_filterMap.put("BAU", Matchers.equalTo("13"));
        assertThat("Matches should be true", l_inputData.matches(l_filterMap));

        Map<String, Matcher> l_filterMap2 = new HashMap<>();
        l_filterMap2.put("NOTBAU", Matchers.equalTo("13"));
        assertThat("We should not have a match if the header does not exist",
                !l_inputData.matches(l_filterMap2));

        Map<String, Matcher> l_filterMap3 = new HashMap<>();
        l_filterMap3.put("BAU", Matchers.equalTo("16"));
        assertThat("We should not have a match if the entry value is incorrect",
                !l_inputData.matches(l_filterMap3));

    }

    @Test
    public void testMatchesAll() {
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

        ////enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();
        l_queryMap.put("AAZ", Matchers.startsWith("12"));

        assertThat("We should have a match", l_inputData.matches(l_queryMap));

        l_queryMap.put("AAZ", Matchers.startsWith("13"));
        assertThat("We should Not have a match", !l_inputData.matches(l_queryMap));

        Map<String, Matcher> l_queryMap2 = new HashMap<>();
        l_queryMap2.put("FFF", Matchers.startsWith("12"));

        assertThat("We should Not have a match", !l_inputData.matches(l_queryMap2));

    }

    @Test
    public void testMatchesAll_multiple() {
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

        ////enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();
        l_queryMap.put("AAZ", Matchers.startsWith("12"));
        l_queryMap.put("BAU", Matchers.endsWith("3"));

        assertThat("We should have a match", l_inputData.matches(l_queryMap));

        l_queryMap.put("DAT", Matchers.containsString("E"));
        assertThat("We should not have a match", !l_inputData.matches(l_queryMap));

    }

    @Test
    public void testMatchesAll_negative() {
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

        ////enrich logData
        // Prepare inputs
        Map<String, Matcher> l_queryMap = new HashMap<>();


        assertThat("We should not have a match because not criteria were given", !l_inputData.matches(l_queryMap));

        assertThat("We should not have a match because not criteria were given", !l_inputData.matches(null));


    }

    @Test
    public void testInsertingOfAPath() {
        ParseDefinition l_definition = new ParseDefinition("tmp");

        ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.addEntry(new ParseDefinitionEntry("ZZZ"));
        ParseDefinitionEntry l_testParseDefinitionEntryBAU = new ParseDefinitionEntry("BAU");
        l_definition.addEntry(l_testParseDefinitionEntryBAU);
        l_definition.defineKeys(l_parseDefinitionEntryKey);

        l_definition.setStoreFilePath(true);
        l_definition.setStorePathFrom("ABC");

        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.updatePath("ABCDEF");

        assertThat("We should have stored the correct value", l_inputData.getFilePath(), is(equalTo("DEF")));

        l_inputData.updatePath("non existant");
        assertThat("We should have stored the correct value", l_inputData.getFilePath(), is(equalTo("non existant")));

        l_inputData.updatePath("ABC/DEF/");
        assertThat("We should have stored the correct value", l_inputData.getFilePath(), is(equalTo("DEF")));

    }
}
