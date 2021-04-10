/**
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

        assertThat("We should have initiated the values", l_cubeData.getEntries().size(), is(equalTo(1)));

        GenericEntry l_valueList = l_cubeData.getEntries().values().iterator().next();

        assertThat("There should be values", l_valueList, is(notNullValue()));

        assertThat("We should have store the LogEntry with the correct key",
                l_cubeData.getEntries().containsKey(l_inputData.get("AAZ")));

        assertThat("We should have the correct value", l_cubeData.getEntries().get(l_inputData.get("AAZ")),
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
    public void testSimpleAccessToDataMain() throws IncorrectParseDefinitionTitleException {

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

        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.fetchValueMap().put("AAZ", "112");
        l_inputData2.fetchValueMap().put("ZZZ", "114");
        l_inputData2.fetchValueMap().put("BAU", "113");
        l_inputData2.fetchValueMap().put("DAT", "AAA");

        LogData<GenericEntry> l_cubeData = new LogData<>(l_inputData);
        l_cubeData.addEntry(l_inputData2);

        assertThat("We should be able to get the values", l_cubeData.get("12"), is(equalTo(l_inputData)));

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
        assertThat("We should have the correct nr of entries", l_logData.getEntries().size(), is(equalTo(5)));
        assertThat("We should have the key for nms:delivery#PrepareFromId",
                l_logData.getEntries().containsKey("nms:delivery#PrepareFromId"));

        assertThat(l_logData.getEntries().get("xtk:persist#NewInstance").getFrequence(), is(equalTo(2)));

        for (GenericEntry lt_entry : l_logData.getEntries().values()) {
            System.out.println(lt_entry.fetchPrintOut());
        }
    }

    /**
     * Testing that we correctly add an entry to the log data
     *
     * Author : gandomi
     *
     */
    @Test
    public void testAddEntry() {

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

        LogData<GenericEntry> l_cubeData = new LogData<GenericEntry>();
        l_cubeData.addEntry(l_inputData);

        assertThat("We should have initiated the values", l_cubeData.getEntries().size(), is(equalTo(1)));

        GenericEntry l_valueList = l_cubeData.getEntries().values().iterator().next();

        assertThat("There should be values", l_valueList, is(notNullValue()));

        assertThat("We should have store the LogEntry with the correct key",
                l_cubeData.getEntries().containsKey(l_inputData.get("AAZ")));

        assertThat("We should have the correct value", l_cubeData.getEntries().get(l_inputData.get("AAZ")),
                is(equalTo(l_inputData)));

    }

    @Test
    public void testAddEntryDuplicated() {

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

        GenericEntry l_inputData3 = new GenericEntry(l_definition);
        l_inputData3.fetchValueMap().put("AAZ", "12");
        l_inputData3.fetchValueMap().put("ZZZ", "14");
        l_inputData3.fetchValueMap().put("BAU", "13");
        l_inputData3.fetchValueMap().put("DAT", "AA");

        LogData<GenericEntry> l_cubeData = new LogData<GenericEntry>();
        l_cubeData.addEntry(l_inputData);
        l_cubeData.addEntry(l_inputData3);

        assertThat("We should have initiated the values", l_cubeData.getEntries().size(), is(equalTo(1)));

        GenericEntry l_valueList = l_cubeData.getEntries().values().iterator().next();

        assertThat("There should be values", l_valueList, is(notNullValue()));

        assertThat("We should have store the LogEntry with the correct key",
                l_cubeData.getEntries().containsKey(l_inputData.get("AAZ")));

        assertThat("We should have the correct value",
                l_cubeData.get(l_inputData.get("AAZ").toString()).equalsBody(l_inputData));

        assertThat("The frequence should have been incremented",
                l_cubeData.get(l_inputData.get("AAZ").toString()).getFrequence(), is(equalTo(2)));

    }

    /**
     * Testing that we can do a group by
     *
     * Author : gandomi
     * @throws IncorrectParseDefinitionTitleException 
     *
     */
    @Test
    public void testgroupBy() throws IncorrectParseDefinitionTitleException {

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

        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.fetchValueMap().put("AAZ", "112");
        l_inputData2.fetchValueMap().put("ZZZ", "114");
        l_inputData2.fetchValueMap().put("BAU", "113");
        l_inputData2.fetchValueMap().put("DAT", "AAA");

        GenericEntry l_inputData3 = new GenericEntry(l_definition);
        l_inputData3.fetchValueMap().put("AAZ", "120");
        l_inputData3.fetchValueMap().put("ZZZ", "14");
        l_inputData3.fetchValueMap().put("BAU", "13");
        l_inputData3.fetchValueMap().put("DAT", "AA");

        LogData<GenericEntry> l_cubeData = new LogData<GenericEntry>();
        l_cubeData.addEntry(l_inputData);
        l_cubeData.addEntry(l_inputData2);
        l_cubeData.addEntry(l_inputData3);

        assertThrows(IncorrectParseDefinitionTitleException.class, () -> l_cubeData.groupBy("KAU"));
        
        LogData<GenericEntry> l_myCube = l_cubeData.groupBy("BAU");

        assertThat("We should have two entries in the new cube one fore 13 and the other for 113",
                l_myCube.getEntries().size(), is(equalTo(2)));

        assertThat("The entry BAU for 13 should be 2", l_myCube.get("13").getFrequence(), is(equalTo(2)));
        
        assertThat("The entry BAU for 113 should be 1", l_myCube.get("113").getFrequence(), is(equalTo(1)));

    }

}
