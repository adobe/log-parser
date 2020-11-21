package com.adobe.campaign.tests.logparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.adobe.campaign.tests.logparser.LogData;
import com.adobe.campaign.tests.logparser.GenericEntry;
import com.adobe.campaign.tests.logparser.ParseDefinition;
import com.adobe.campaign.tests.logparser.ParseDefinitionEntry;
import com.adobe.campaign.tests.logparser.exceptions.IncorrectParseDefinitionTitleException;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;

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
    public void testHelloWorldSTDConstructor() {

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

        Map<String, GenericEntry> l_geMap = new HashMap<String, GenericEntry>();
        l_geMap.put(l_inputData.makeKey(), l_inputData);
        LogData<GenericEntry> l_cubeData2 = new LogData<GenericEntry>(l_geMap);

        assertThat("We should have initiated the values", l_cubeData2, is(notNullValue()));

        assertThat("We should have the correct value", l_cubeData, is(equalTo(l_cubeData2)));

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
    public void testSimpleAccessToData_NegativeNoExistingKeyCube()
            throws IncorrectParseDefinitionTitleException {

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

        assertThat("We should not have a an entry here as there are no entries with the given key",
                l_cubeData.get("14", "ZZZ"), is(nullValue()));

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

    @Test
    public void testLogDataFactory()
            throws InstantiationException, IllegalAccessException, StringParseException {

        //Create a parse definition

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_pDefinition = new ParseDefinition("ACC Coverage");
        l_pDefinition.setDefinitionEntries(Arrays.asList(l_apiDefinition, l_verbDefinition));

        final String apacheLogFile = "src/test/resources/logTests/acc/acc_integro_jenkins_log_exerpt.txt";

        LogData<GenericEntry> l_logData = LogDataFactory.generateLogData(Arrays.asList(apacheLogFile),
                l_pDefinition);

        assertThat(l_logData, is(notNullValue()));
        assertThat("We should have the correct nr of entries", l_logData.getCubeData().size(),
                is(equalTo(5)));
        assertThat("We should have the key for nms:delivery#PrepareFromId",
                l_logData.getCubeData().containsKey("nms:delivery#PrepareFromId"));

        assertThat(l_logData.getCubeData().get("xtk:persist#NewInstance").getFrequence(), is(equalTo(2)));

        for (GenericEntry lt_entry : l_logData.getCubeData().values()) {
            System.out.println(lt_entry.fetchPrintOut());
        }
    }
}
