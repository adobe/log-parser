package com.adobe.campaign.tests.logparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertThrows;



import org.testng.annotations.Test;

import com.adobe.campaign.tests.logparser.LogData;
import com.adobe.campaign.tests.logparser.GenericEntry;
import com.adobe.campaign.tests.logparser.ParseDefinition;
import com.adobe.campaign.tests.logparser.ParseDefinitionEntry;
import com.adobe.campaign.tests.logparser.exceptions.IncorrectParseDefinitionTitleException;

public class LogDataTest {

    /**
     * Testing that we correctly create a cube
     *
     * Author : gandomi
     *
     */
    @Test
    public void testHelloWorld() {

        ParseDefinition l_definition = new ParseDefinition("tmp");
        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.addEntry(new ParseDefinitionEntry("ZZZ"));
        l_definition.addEntry(new ParseDefinitionEntry("BAU"));
        l_definition.addEntry(new ParseDefinitionEntry("DAT"));
        l_definition.defineKeys(l_parseDefinitionEntryKey);

        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.fetchValueMap().put("AAZ", "12");
        l_inputData.fetchValueMap().put("ZZZ", "14");
        l_inputData.fetchValueMap().put("BAU", "13");
        l_inputData.fetchValueMap().put("DAT", "AA");

        LogData<GenericEntry> l_cubeData = new LogData<>(l_inputData);

        assertThat("We should have initiated the values", l_cubeData.getCubeData().size(), is(equalTo(1)));

        GenericEntry l_valueList = l_cubeData.getCubeData().values().iterator().next();

        assertThat("There should be values", l_valueList, is(notNullValue()));

        assertThat("We should have store the LogEntry with the correct key",
                l_cubeData.getCubeData().containsKey(l_inputData.get("AAZ")));

        assertThat("We should have the correct value", l_cubeData.getCubeData().get(l_inputData.get("AAZ")),
                is(equalTo(l_inputData)));

    }

    
    @Test
    public void testSimpleAccessToData() throws IncorrectParseDefinitionTitleException {

        ParseDefinition l_definition = new ParseDefinition("tmp");
        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.addEntry(new ParseDefinitionEntry("ZZZ"));
        l_definition.addEntry(new ParseDefinitionEntry("BAU"));
        l_definition.addEntry(new ParseDefinitionEntry("DAT"));
        l_definition.defineKeys(l_parseDefinitionEntryKey);

        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.fetchValueMap().put("AAZ", "12");
        l_inputData.fetchValueMap().put("ZZZ", "14");
        l_inputData.fetchValueMap().put("BAU", "13");
        l_inputData.fetchValueMap().put("DAT", "AA");

        LogData<GenericEntry> l_cubeData = new LogData<>(l_inputData);

        assertThat("We should be able to get the values", l_cubeData.get("12", "ZZZ"), is(equalTo("14")));

    }
    
    
    @Test
    public void testSimpleAccessToData_NegativeNoExistingKeyCube() throws IncorrectParseDefinitionTitleException {

        ParseDefinition l_definition = new ParseDefinition("tmp");
        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.addEntry(new ParseDefinitionEntry("ZZZ"));
        l_definition.addEntry(new ParseDefinitionEntry("BAU"));
        l_definition.addEntry(new ParseDefinitionEntry("DAT"));
        l_definition.defineKeys(l_parseDefinitionEntryKey);

        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.fetchValueMap().put("AAZ", "12");
        l_inputData.fetchValueMap().put("ZZZ", "14");
        l_inputData.fetchValueMap().put("BAU", "13");
        l_inputData.fetchValueMap().put("DAT", "AA");

        LogData<GenericEntry> l_cubeData = new LogData<>(l_inputData);

        assertThat("We should not have a an entry here as there are no entries with the given key",l_cubeData.get("14", "ZZZ"),is(nullValue()));

    }
    
    
    @Test
    public void testSimpleAccessToData_NegativeBadTitleForEntryTitles() {

        ParseDefinition l_definition = new ParseDefinition("tmp");
        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.addEntry(new ParseDefinitionEntry("ZZZ"));
        l_definition.addEntry(new ParseDefinitionEntry("BAU"));
        l_definition.addEntry(new ParseDefinitionEntry("DAT"));
        l_definition.defineKeys(l_parseDefinitionEntryKey);

        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.fetchValueMap().put("AAZ", "12");
        l_inputData.fetchValueMap().put("ZZZ", "14");
        l_inputData.fetchValueMap().put("BAU", "13");
        l_inputData.fetchValueMap().put("DAT", "AA");

        LogData<GenericEntry> l_cubeData = new LogData<>(l_inputData);

        assertThrows(IncorrectParseDefinitionTitleException.class, () -> l_cubeData.get("12", "NONo"));

    }
}
