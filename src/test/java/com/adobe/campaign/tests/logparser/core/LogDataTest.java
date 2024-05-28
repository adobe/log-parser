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
package com.adobe.campaign.tests.logparser.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.adobe.campaign.tests.logparser.exceptions.LogDataExportToFileException;
import com.adobe.campaign.tests.logparser.utils.CSVManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.adobe.campaign.tests.logparser.exceptions.IncorrectParseDefinitionException;
import com.adobe.campaign.tests.logparser.exceptions.ParseDefinitionImportExportException;
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
    public void testSimpleAccessToDataMain() throws IncorrectParseDefinitionException {

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
    public void testSimpleAccessToData() throws IncorrectParseDefinitionException {

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
    public void testSimpleChangeOfData() throws IncorrectParseDefinitionException {

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

        l_cubeData.put("12", "ZZZ", "14,5");

        assertThat("We should be able to get the values", l_cubeData.get("12", "ZZZ"), is(equalTo("14,5")));

        assertThrows(IncorrectParseDefinitionException.class, () -> l_cubeData.put("1", "ZZZ", "14,5"));

        assertThrows(IncorrectParseDefinitionException.class, () -> l_cubeData.put("12", "PPP", "14,5"));

    }

    @Test
    public void testSimpleAccessToData_NegativeNoExistingKeyCube() throws IncorrectParseDefinitionException {

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

        assertThrows(IncorrectParseDefinitionException.class, () -> l_cubeData.get("12", "NONo"));

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
    
    
    @Test
    public void testLogDataFactoryWithJSONFileForParseDefinition()
            throws InstantiationException, IllegalAccessException, StringParseException,
            ParseDefinitionImportExportException {

        
        final String apacheLogFile = "src/test/resources/logTests/acc/acc_integro_jenkins_log_exerpt.txt";
        final String l_jsonPath = "src/test/resources/parseDefinitions/parseDefinitionLogDataFactory.json";
        
        LogData<GenericEntry> l_logData = LogDataFactory.generateLogData(Arrays.asList(apacheLogFile),
                l_jsonPath);

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
                l_cubeData.get(l_inputData.get("AAZ").toString()).fetchValueMap(),
                is(equalTo(l_inputData.fetchValueMap())));

        assertThat("The frequence should have been incremented",
                l_cubeData.get(l_inputData.get("AAZ").toString()).getFrequence(), is(equalTo(2)));

    }

    /**
     * Testing that we can do a group by
     *
     * Author : gandomi
     * 
     * @throws IncorrectParseDefinitionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    @Test
    public void testgroupBy()
            throws IncorrectParseDefinitionException, InstantiationException, IllegalAccessException {

        ParseDefinition l_definition = new ParseDefinition("tmp");

        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.addEntry(new ParseDefinitionEntry("ZZZ"));
        final ParseDefinitionEntry l_testParseDefinitionEntry = new ParseDefinitionEntry("BAU");
        l_definition.addEntry(l_testParseDefinitionEntry);
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

        assertThrows(IncorrectParseDefinitionException.class,
                () -> l_cubeData.groupBy("KAU", GenericEntry.class));

        LogData<GenericEntry> l_myCube = l_cubeData.groupBy("BAU", GenericEntry.class);

        assertThat(l_myCube.getEntries().values().iterator().next().getParseDefinition()
                .getDefinitionEntries().size(), is(equalTo(1)));
        assertThat(l_myCube.getEntries().values().iterator().next().getParseDefinition()
                .getDefinitionEntries().get(0), is(equalTo(l_testParseDefinitionEntry)));

        assertThat("We should have two entries in the new cube one fore 13 and the other for 113",
                l_myCube.getEntries().size(), is(equalTo(2)));

        assertThat("The entry BAU for 13 should be 2", l_myCube.get("13").getFrequence(), is(equalTo(2)));

        assertThat("The entry BAU for 113 should be 1", l_myCube.get("113").getFrequence(), is(equalTo(1)));

    }

    /**
     * Testing that we can do a group by
     *
     * Author : gandomi
     * 
     * @throws IncorrectParseDefinitionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    @Test
    public void testgroupBy_Default()
            throws IncorrectParseDefinitionException, InstantiationException, IllegalAccessException {

        ParseDefinition l_definition = new ParseDefinition("tmp");

        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.addEntry(new ParseDefinitionEntry("ZZZ"));
        final ParseDefinitionEntry l_testParseDefinitionEntry = new ParseDefinitionEntry("BAU");
        l_definition.addEntry(l_testParseDefinitionEntry);
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

        assertThrows(IncorrectParseDefinitionException.class, () -> l_cubeData.groupBy("KAU"));

        LogData<GenericEntry> l_myCube = l_cubeData.groupBy("BAU");

        assertThat(l_myCube.getEntries().values().iterator().next().getParseDefinition()
                .getDefinitionEntries().size(), is(equalTo(1)));
        assertThat(l_myCube.getEntries().values().iterator().next().getParseDefinition()
                .getDefinitionEntries().get(0), is(equalTo(l_testParseDefinitionEntry)));

        assertThat("We should have two entries in the new cube one fore 13 and the other for 113",
                l_myCube.getEntries().size(), is(equalTo(2)));

        assertThat("The entry BAU for 13 should be 2", l_myCube.get("13").getFrequence(), is(equalTo(2)));

        assertThat("The entry BAU for 113 should be 1", l_myCube.get("113").getFrequence(), is(equalTo(1)));

    }

    /**
     * Testing that we can do a group by with two values
     *
     * Author : gandomi
     * 
     * @throws IncorrectParseDefinitionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    @Test
    public void testMultipleGroupBy()
            throws IncorrectParseDefinitionException, InstantiationException, IllegalAccessException {

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

        LogData<GenericEntry> l_myCube = l_cubeData.groupBy(Arrays.asList("BAU", "DAT"), GenericEntry.class);

        final ParseDefinition l_gpParseDefinition = l_myCube.getEntries().values().iterator().next()
                .getParseDefinition();
        assertThat(l_gpParseDefinition.getDefinitionEntries().size(), is(equalTo(2)));

        assertThat("The key since not defined is the parse definition entries in the order of the group by",
                l_gpParseDefinition.fetchKeyOrder(),
                Matchers.contains(l_testParseDefinitionEntryBAU.getTitle(), l_testParseDefinitionEntryDAT.getTitle()));

        assertThat(l_gpParseDefinition.getDefinitionEntries().get(0),
                is(equalTo(l_testParseDefinitionEntryBAU)));

        assertThat("We should have two entries in the new cube one fore 13 and the other for 113",
                l_myCube.getEntries().size(), is(equalTo(2)));

        assertThat("The entry BAU for 13 should be 2", l_myCube.get("13#AA").getFrequence(), is(equalTo(2)));

        assertThat("The entry 13#AA should have two value maps (one pe definition)",
                l_myCube.get("13#AA").valuesMap.keySet().size(), equalTo(2));
        assertThat("The entry BAU for 113 should be 1", l_myCube.get("113#AAA").getFrequence(),
                is(equalTo(1)));

    }

    /**
     * Testing that we can do a group by with two values
     *
     * Author : gandomi
     * 
     * @throws IncorrectParseDefinitionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    @Test
    public void testGroupBy_Chaining()
            throws IncorrectParseDefinitionException, InstantiationException, IllegalAccessException {

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

        LogData<GenericEntry> l_myCube = l_cubeData.groupBy(Arrays.asList("BAU", "DAT")).groupBy("DAT");

        final ParseDefinition l_gpParseDefinition = l_myCube.getEntries().values().iterator().next()
                .getParseDefinition();
        assertThat(l_gpParseDefinition.getDefinitionEntries().size(), is(equalTo(1)));

        assertThat("The key since not defined is the parse definition entries in the order of the group by",
                l_gpParseDefinition.fetchKeyOrder(), Matchers.contains(l_testParseDefinitionEntryDAT.getTitle()));

        assertThat(l_gpParseDefinition.getDefinitionEntries().get(0),
                is(equalTo(l_testParseDefinitionEntryDAT)));

        assertThat("We should have two entries in the new cube one fore AA and the other for AAA",
                l_myCube.getEntries().size(), is(equalTo(2)));

        assertThat("The entry DAT for AA should be 2", l_myCube.get("AA").getFrequence(), is(equalTo(2)));

        assertThat("The entry DAT for AAA should be 1", l_myCube.get("AAA").getFrequence(), is(equalTo(1)));

    }

    /**
     * Testing that we can do a group by with two values
     *
     * Author : gandomi
     * 
     * @throws IncorrectParseDefinitionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    @Test
    public void testMultipleGroupBy_Default()
            throws IncorrectParseDefinitionException, InstantiationException, IllegalAccessException {

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

        LogData<GenericEntry> l_myCube = l_cubeData.groupBy(Arrays.asList("BAU", "DAT"));

        final ParseDefinition l_gpParseDefinition = l_myCube.getEntries().values().iterator().next()
                .getParseDefinition();
        assertThat(l_gpParseDefinition.getDefinitionEntries().size(), is(equalTo(2)));

        assertThat("The key since not defined is the parse definition entries in the order of the group by",
                l_gpParseDefinition.fetchKeyOrder(),
                Matchers.contains(l_testParseDefinitionEntryBAU.getTitle(), l_testParseDefinitionEntryDAT.getTitle()));

        assertThat(l_gpParseDefinition.getDefinitionEntries().get(0),
                is(equalTo(l_testParseDefinitionEntryBAU)));

        assertThat("We should have two entries in the new cube one fore 13 and the other for 113",
                l_myCube.getEntries().size(), is(equalTo(2)));

        assertThat("The entry BAU for 13 should be 2", l_myCube.get("13#AA").getFrequence(), is(equalTo(2)));

        assertThat("The entry BAU for 113 should be 1", l_myCube.get("113#AAA").getFrequence(),
                is(equalTo(1)));

    }

    /**
     * Testing that we can do a filter
     *
     * Author : gandomi
     * 
     * @throws IncorrectParseDefinitionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    @Test
    public void testFilter_Default() {

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

        Map<String, Object> l_filterProperties = new HashMap<>();
        l_filterProperties.put("ZZZ", "114");
        LogData<GenericEntry> l_myCube = l_cubeData.filterBy(l_filterProperties);

        assertThat("We should have found one entry", l_myCube.getEntries().size(), is(equalTo(1)));

        assertThat("We should have found the correct entry", l_myCube.getEntries().containsKey("112"));

        //Adding another filter
        l_filterProperties.put("DAT", "AAA");

        LogData<GenericEntry> l_myCube2 = l_cubeData.filterBy(l_filterProperties);

        assertThat("We should have found one entry", l_myCube2.getEntries().size(), is(equalTo(1)));

        assertThat("We should have found the correct entry", l_myCube2.getEntries().containsKey("112"));

        //Negative test
        l_filterProperties.put("DAT", "AA");

        LogData<GenericEntry> l_myCube3 = l_cubeData.filterBy(l_filterProperties);

        assertThat("We should have found one entry", l_myCube3.getEntries().size(), is(equalTo(0)));

    }

    /**
     * Testing that we can do a filter using multiple fields
     *
     * Author : gandomi
     * 
     * @throws IncorrectParseDefinitionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    @Test
    public void testFilter_Multi() {

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

        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.fetchValueMap().put("AAZ", "112");
        l_inputData2.fetchValueMap().put("ZZZ", "114");
        l_inputData2.fetchValueMap().put("BAU", "113");
        l_inputData2.fetchValueMap().put("DAT", "AAA");

        GenericEntry l_inputData3 = new GenericEntry(l_definition);
        l_inputData3.fetchValueMap().put("AAZ", "120");
        l_inputData3.fetchValueMap().put("ZZZ", "14");
        l_inputData3.fetchValueMap().put("BAU", "13");
        l_inputData3.fetchValueMap().put("DAT", "AAA");

        LogData<GenericEntry> l_cubeData = new LogData<GenericEntry>();
        l_cubeData.addEntry(l_inputData);
        l_cubeData.addEntry(l_inputData2);
        l_cubeData.addEntry(l_inputData3);

        Map<String, Object> l_filterProperties = new HashMap<>();
        l_filterProperties.put("ZZZ", "14");
        l_filterProperties.put("BAU", "13");

        LogData<GenericEntry> l_myCube = l_cubeData.filterBy(l_filterProperties);

        assertThat("We should have found one entry", l_myCube.getEntries().size(), is(equalTo(2)));

        assertThat("We should have found the correct entry", l_myCube.getEntries().containsKey("120"));

        l_filterProperties.remove("ZZZ");
        LogData<GenericEntry> l_myCube2 = l_cubeData.filterBy(l_filterProperties);

        assertThat("We should have found one entry", l_myCube2.getEntries().size(), is(equalTo(2)));

        assertThat("We should have found the correct entry", l_myCube2.getEntries().containsKey("120"));

        l_filterProperties.put("DAT", "AAA");
        LogData<GenericEntry> l_myCube3 = l_cubeData.filterBy(l_filterProperties);

        assertThat("We should have found one entry", l_myCube3.getEntries().size(), is(equalTo(1)));

        assertThat("We should have found the correct entry", l_myCube3.getEntries().containsKey("120"));

    }

    @Test
    public void testIllegalConstrtuctorOfLogDataFactory() {
        Assert.assertThrows(IllegalStateException.class, () -> new LogDataFactory());
    }

    @Test
    public void testEquals() {

        ParseDefinition l_definition = new ParseDefinition("tmp");

        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.addEntry(new ParseDefinitionEntry("ZZZ"));
        l_definition.defineKeys(l_parseDefinitionEntryKey);

        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.fetchValueMap().put("AAZ", "12");
        l_inputData.fetchValueMap().put("ZZZ", "14");

        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.fetchValueMap().put("AAZ", "112");
        l_inputData2.fetchValueMap().put("ZZZ", "114");

        LogData<GenericEntry> l_cubeData = new LogData<GenericEntry>();
        l_cubeData.addEntry(l_inputData);
        l_cubeData.addEntry(l_inputData2);

        LogData<GenericEntry> l_cubeData2 = new LogData<GenericEntry>();
        l_cubeData2.addEntry(l_inputData);
        l_cubeData2.addEntry(l_inputData2);

        assertThat("Both objects should be equal", l_cubeData, equalTo(l_cubeData));

        assertThat("Both objects should be equal", l_cubeData, equalTo(l_cubeData2));

        assertThat("Both objects should be equal", !l_cubeData.equals(null));

        assertThat("Both objects should be equal", null, not(equalTo(l_cubeData2)));

        assertThat("Both objects should be equal", l_cubeData, not(equalTo(l_inputData)));

        l_cubeData.setEntries(null);
        assertThat("Both objects should be equal", l_cubeData, not(equalTo(l_cubeData2)));

        GenericEntry l_inputData3 = new GenericEntry(l_definition);
        l_inputData3.fetchValueMap().put("AAZ", "1122");
        l_inputData3.fetchValueMap().put("ZZZ", "1142");

        LogData<GenericEntry> l_cubeData3 = new LogData<GenericEntry>();
        l_cubeData3.addEntry(l_inputData);
        l_cubeData3.addEntry(l_inputData3);

        assertThat("Both objects should be equal", l_cubeData3, not(equalTo(l_cubeData2)));

    }
    
    /**
     * Testing that we can do a search
     *
     * Author : gandomi
     * 
     * @throws IncorrectParseDefinitionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    @Test
    public void testSearch_Default() {

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

        LogData<GenericEntry> l_myCube = l_cubeData.searchEntries("ZZZ", "114");

        assertThat("We should have found one entry", l_myCube.getEntries().size(), is(equalTo(1)));

        assertThat("We should have found the correct entry", l_myCube.getEntries().containsKey("112"));

        //Adding another filter

        LogData<GenericEntry> l_myCube2 = l_cubeData.searchEntries("DAT", "AAA");

        assertThat("We should have found one entry", l_myCube2.getEntries().size(), is(equalTo(1)));

        assertThat("We should have found the correct entry", l_myCube2.getEntries().containsKey("112"));

        //Negative test
        LogData<GenericEntry> l_myCube3 = l_cubeData.searchEntries("DAT", "AAL");

        assertThat("We should have found one entry", l_myCube3.getEntries().size(), is(equalTo(0)));

    }
    
    
    /**
     * Testing that we can do a search using multiple fields
     *
     * Author : gandomi
     * 
     * @throws IncorrectParseDefinitionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    @Test
    public void testsearch_Multi() {

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

        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.fetchValueMap().put("AAZ", "112");
        l_inputData2.fetchValueMap().put("ZZZ", "114");
        l_inputData2.fetchValueMap().put("BAU", "113");
        l_inputData2.fetchValueMap().put("DAT", "AAA");

        GenericEntry l_inputData3 = new GenericEntry(l_definition);
        l_inputData3.fetchValueMap().put("AAZ", "120");
        l_inputData3.fetchValueMap().put("ZZZ", "14");
        l_inputData3.fetchValueMap().put("BAU", "13");
        l_inputData3.fetchValueMap().put("DAT", "AAA");

        LogData<GenericEntry> l_cubeData = new LogData<GenericEntry>();
        l_cubeData.addEntry(l_inputData);
        l_cubeData.addEntry(l_inputData2);
        l_cubeData.addEntry(l_inputData3);

        Map<String, Object> l_filterProperties = new HashMap<>();
        l_filterProperties.put("ZZZ", "14");
        l_filterProperties.put("BAU", "13");

        LogData<GenericEntry> l_myCube = l_cubeData.searchEntries(l_filterProperties);

        assertThat("We should have found one entry", l_myCube.getEntries().size(), is(equalTo(2)));

        assertThat("We should have found the correct entry", l_myCube.getEntries().containsKey("120"));

        l_filterProperties.remove("ZZZ");
        LogData<GenericEntry> l_myCube2 = l_cubeData.searchEntries(l_filterProperties);

        assertThat("We should have found one entry", l_myCube2.getEntries().size(), is(equalTo(2)));

        assertThat("We should have found the correct entry", l_myCube2.getEntries().containsKey("120"));

        l_filterProperties.put("DAT", "AAA");
        LogData<GenericEntry> l_myCube3 = l_cubeData.searchEntries(l_filterProperties);

        assertThat("We should have found one entry", l_myCube3.getEntries().size(), is(equalTo(1)));

        assertThat("We should have found the correct entry", l_myCube3.getEntries().containsKey("120"));

    }
    
    
    /**
     * Testing that we can detect if an element is present
     *
     * Author : gandomi
     * 
     * @throws IncorrectParseDefinitionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    @Test
    public void testIsPresent() {

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

        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.fetchValueMap().put("AAZ", "112");
        l_inputData2.fetchValueMap().put("ZZZ", "114");
        l_inputData2.fetchValueMap().put("BAU", "113");
        l_inputData2.fetchValueMap().put("DAT", "AAA");

        GenericEntry l_inputData3 = new GenericEntry(l_definition);
        l_inputData3.fetchValueMap().put("AAZ", "120");
        l_inputData3.fetchValueMap().put("ZZZ", "14");
        l_inputData3.fetchValueMap().put("BAU", "13");
        l_inputData3.fetchValueMap().put("DAT", "AAA");

        LogData<GenericEntry> l_cubeData = new LogData<GenericEntry>();
        l_cubeData.addEntry(l_inputData);
        l_cubeData.addEntry(l_inputData2);
        l_cubeData.addEntry(l_inputData3);

        Map<String, Object> l_filterProperties = new HashMap<>();
        l_filterProperties.put("ZZZ", "14");
        l_filterProperties.put("BAU", "13");

        assertThat("We should state that the given entry is present", l_cubeData.isEntryPresent(l_filterProperties));
        
        l_filterProperties.put("BAU", "1398");
        
        assertThat("We should state that the given entry is present", !l_cubeData.isEntryPresent(l_filterProperties));

        assertThat("We should state that the given entry is present", l_cubeData.isEntryPresent("BAU","13"));

        assertThat("We should state that an entry is NOT  present", !l_cubeData.isEntryPresent("BAU","999"));
    }


    @Test
    public void testNestedFileAccess()
            throws InstantiationException, IllegalAccessException, StringParseException {

        //Create a parse definition

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart(" /rest/head/");
        l_apiDefinition.setEnd(" ");

        ParseDefinition l_pDefinition = new ParseDefinition("Simple log");
        l_pDefinition.setDefinitionEntries(Arrays.asList(l_verbDefinition2, l_apiDefinition));
        l_pDefinition.defineKeys(Arrays.asList(l_apiDefinition, l_verbDefinition2));

        final String apacheLogFile = "src/test/resources/nestedDirs/dirA/simpleLog.log";

        Map<String, GenericEntry> l_entries = StringParseFactory
                .extractLogEntryMap(Arrays.asList(apacheLogFile), l_pDefinition, GenericEntry.class);

        assertThat(l_entries, is(notNullValue()));
        assertThat("We should have entries", l_entries.size(), is(greaterThan(0)));
        assertThat("We should have entries", l_entries.size(), is(lessThan(19)));
        String l_searchItem1 = "extAccount/destroySharedAudience#GET";
        assertThat("We should have the key for amcDataSource",
                l_entries.containsKey(l_searchItem1));

        GenericEntry l_ge = l_entries.get(l_searchItem1);
        assertThat("We should only have one entry for the "+l_searchItem1, l_ge.getFrequence(), Matchers.equalTo(1));

        LogData<GenericEntry> l_logData = LogDataFactory.generateLogData(Arrays.asList(apacheLogFile),
                l_pDefinition);

        assertThat("We should have the key for amcDataSource",
                l_logData.getEntries().containsKey(l_searchItem1));

        GenericEntry l_logDataItem = l_logData.get(l_searchItem1);
        assertThat("We should only have one entry for the "+l_searchItem1, l_logDataItem.getFrequence(), Matchers.equalTo(1));

        String l_searchItem2 = "extAccount/importSharedAudience#GET";

        assertThat("We should have the key for amcDataSource",
                l_logData.getEntries().containsKey(l_searchItem2));

        GenericEntry l_logDataItem2 = l_logData.get(l_searchItem2);
        assertThat("We should only have one entry for the "+l_searchItem2, l_logDataItem2.getFrequence(), Matchers.equalTo(1));

        File l_rootDir = new File("src/test/resources/nestedDirs/");

        assertThat("The directory should exist", l_rootDir.exists());
        assertThat("The directory should be a directory", l_rootDir.isDirectory());
        String l_fileFilter = "simple*.log";
        Iterator<File> l_foundFilesIterator = FileUtils.iterateFiles(l_rootDir, new WildcardFileFilter(l_fileFilter), TrueFileFilter.INSTANCE);

        l_foundFilesIterator.forEachRemaining(f -> System.out.println(f.getAbsolutePath()));

        //Nested search

        LogData<GenericEntry> l_logData2 = LogDataFactory.generateLogData("src/test/resources/nestedDirs/", l_fileFilter,
                l_pDefinition);


        assertThat("We should have the key for amcDataSource",
                l_logData2.getEntries().containsKey(l_searchItem1));

        GenericEntry l_logDataItem2_1 = l_logData2.get(l_searchItem1);
        assertThat("We should only have one entry for the "+l_searchItem1, l_logDataItem2_1.getFrequence(), Matchers.equalTo(1));

        assertThat("We should have the key for amcDataSource",
                l_logData2.getEntries().containsKey(l_searchItem2));

        GenericEntry l_logDataItem2_2 = l_logData2.get(l_searchItem2);
        assertThat("We should only have one entry for the "+l_searchItem2, l_logDataItem2_2.getFrequence(), Matchers.equalTo(2));
    }

    @Test
    public void testLogDataFactoryWithJSONFileForParseDefinitionAndSearchFile()
            throws InstantiationException, IllegalAccessException, StringParseException,
            ParseDefinitionImportExportException {

        String l_rootPath = "src/test/resources/nestedDirs/";
        String l_fileFilter = "simple*.log";

        final String l_jsonPath = "src/test/resources/parseDefinitions/simpleParseDefinitionLogDataFactory.json";

        LogData<GenericEntry> l_logData = LogDataFactory.generateLogData(l_rootPath, l_fileFilter,
                l_jsonPath);

        String l_searchItem1 = "extAccount/destroySharedAudience#GET";
        String l_searchItem2 = "extAccount/importSharedAudience#GET";


        assertThat("We should have the key for amcDataSource",
                l_logData.getEntries().containsKey(l_searchItem1));

        GenericEntry l_logDataItem2_1 = l_logData.get(l_searchItem1);
        assertThat("We should only have one entry for the "+l_searchItem1, l_logDataItem2_1.getFrequence(), equalTo(1));

        assertThat("We should have the key for amcDataSource",
                l_logData.getEntries().containsKey(l_searchItem2));

        GenericEntry l_logDataItem2_2 = l_logData.get(l_searchItem2);
        assertThat("We should only have one entry for the "+l_searchItem2, l_logDataItem2_2.getFrequence(), equalTo(2));
    }


    /******************** Search file tests ***********************/

    @Test
    public void testFileSearch() {
        String l_fileFilter = "simple*.log";
        String l_rootPath = "src/test/resources/nestedDirs/";

        List<String> l_foundFilePaths = LogDataFactory.findFilePaths(l_rootPath, l_fileFilter);

        assertThat("We should have found 2 files", l_foundFilePaths,
                Matchers.containsInAnyOrder(Matchers.endsWith("dirA/simpleLog.log"),
                        Matchers.endsWith("dirB/simpleLog.log")));
    }

    @Test
    public void testFileSearch_negative() {
        String l_fileFilter = "simple*.log";
        String l_rootPath = "src/test/resources/nonexistantDir/";

        Assert.assertThrows(IllegalArgumentException.class, () -> LogDataFactory.findFilePaths(l_rootPath, l_fileFilter));
    }

    @Test
    public void testFileSearch_negative2() {
        String l_fileFilter = "simple*.log";
        String l_rootPath = "src/test/resources/testng.xml";

        Assert.assertThrows(IllegalArgumentException.class, () -> LogDataFactory.findFilePaths(l_rootPath, l_fileFilter));
    }

    @Test
    public void testFileSearch_null() {
        String l_fileFilter = null;
        String l_rootPath = "src/test/resources/nestedDirs";
        assertThat("An empty or null filter should not return enything",
                LogDataFactory.findFilePaths(l_rootPath, l_fileFilter), Matchers.empty());
    }

    @Test
    public void testFileSearch_empty() {
        String l_fileFilter = "";
        String l_rootPath = "src/test/resources/nestedDirs";
        assertThat("An empty or null filter should not return enything",
                LogDataFactory.findFilePaths(l_rootPath, l_fileFilter), Matchers.empty());
    }

    @Test
    public void testFileSearch_negative4() {
        String l_fileFilter="nonExistantFile.notLog";
        String l_rootPath = "src/test/resources/nestedDirs";
        List<String> l_foundFilePaths = LogDataFactory.findFilePaths(l_rootPath, l_fileFilter);

        assertThat("We should have found no files", l_foundFilePaths.size(),
                Matchers.equalTo(0));
    }

    @Test
    public void testFileSearch_negative5() {
        String l_fileFilter = "simple*.log";
        String l_rootPath = "null";

        Assert.assertThrows(IllegalArgumentException.class, () -> LogDataFactory.findFilePaths(l_rootPath, l_fileFilter));
    }

    @Test
    public void testFileSearch_specific() {
        String l_fileFilter = "simpleLog.log";
        String l_rootPath = "src/test/resources/nestedDirs";

        List<String> l_foundFilePaths = LogDataFactory.findFilePaths(l_rootPath, l_fileFilter);
        assertThat("We should have found 2 files", l_foundFilePaths,
                Matchers.containsInAnyOrder(Matchers.endsWith("dirA/simpleLog.log"),
                        Matchers.endsWith("dirB/simpleLog.log")));
    }

    /*************** #55 Exporting results **********************/
    @Test
    public void testExportData()
            throws StringParseException, InstantiationException, IllegalAccessException, IOException,
            LogDataExportToFileException {
        //Create a parse definition

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        final String apacheLogFile = "src/test/resources/nestedDirs/dirA/simpleLog.log";

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart(" /rest/head/");
        l_apiDefinition.setEnd(" ");

        ParseDefinition l_pDefinition = new ParseDefinition("Simple log");
        l_pDefinition.setDefinitionEntries(Arrays.asList(l_verbDefinition2, l_apiDefinition));
        l_pDefinition.defineKeys(Arrays.asList(l_apiDefinition, l_verbDefinition2));

        String l_rootPath = "src/test/resources/nestedDirs/";
        String l_fileFilter = "simple*.log";

        final String l_jsonPath = "src/test/resources/parseDefinitions/simpleParseDefinitionLogDataFactory.json";

        LogData<GenericEntry> l_logData = LogDataFactory.generateLogData("src/test/resources/nestedDirs/", l_fileFilter,
                l_pDefinition);

        int l_nrOfEntries = l_logData.getEntries().keySet().size();
        assertThat("The LogData needs to have been generated", l_nrOfEntries, Matchers.greaterThan(0));

        File l_exportedFile = l_logData.exportLogDataToCSV();

        assertThat("We successfully created the file", l_exportedFile, notNullValue());
        assertThat("We successfully created the file", l_exportedFile.exists());
        assertThat("We successfully created the file correctly", l_exportedFile.isFile());
        try {
            String l_fileNameToExpect = l_pDefinition.getTitle().replace(' ', '-');
            assertThat("We successfully created the file correctly", l_exportedFile.getName(),
                    Matchers.endsWith(l_fileNameToExpect + "-export.csv"));

            Map<String, List<String>> l_fetchedResult = CSVManager.fetchCSVToMapList(StdLogEntry.STD_DATA_KEY,
                    l_exportedFile);

            for (GenericEntry l_ge : l_logData.getEntries().values()) {
                assertThat(l_ge.fetchValuesAsList(), Matchers.equalTo(l_fetchedResult.get(l_ge.makeKey())));
            }
        } finally {
            l_exportedFile.delete();
        }

    }

    @Test
    public void testExportDataFileExists()
            throws StringParseException, InstantiationException, IllegalAccessException, IOException,
            LogDataExportToFileException {
        String l_rootPath = "src/test/resources/nestedDirs/";
        String l_fileFilter = "simple*.log";

        final String l_jsonPath = "src/test/resources/parseDefinitions/simpleParseDefinitionLogDataFactory.json";

        LogData<GenericEntry> l_logData = LogDataFactory.generateLogData(l_rootPath, l_fileFilter,
                l_jsonPath);

        String l_searchItem1 = "extAccount/destroySharedAudience#GET";
        String l_searchItem2 = "extAccount/importSharedAudience#GET";

        ParseDefinition l_pDefinition = l_logData.getEntries().values().stream().findFirst().get().getParseDefinition();
        String l_fileNameToExpect = l_pDefinition.getTitle().replace(' ', '-')+"-export.csv";

        //Create the file so it is deleted
        File l_duplicateFile = new File(l_fileNameToExpect);
        l_duplicateFile.createNewFile();

        assertThat("We should have the key for amcDataSource",
                l_logData.getEntries().containsKey(l_searchItem1));

        File l_exportedFile = l_logData.exportLogDataToCSV();

        assertThat("We successfully created the file", l_exportedFile, notNullValue());
        assertThat("We successfully created the file", l_exportedFile.exists());
        assertThat("We successfully created the file correctly", l_exportedFile.isFile());
        try {

            assertThat("We successfully created the file correctly", l_exportedFile.getName(),
                    Matchers.endsWith(l_fileNameToExpect));

            Map<String, List<String>> l_fetchedResult = CSVManager.fetchCSVToMapList(StdLogEntry.STD_DATA_KEY,
                    l_exportedFile);

            for (GenericEntry l_ge : l_logData.getEntries().values()) {
                assertThat(l_ge.fetchValuesAsList(), Matchers.equalTo(l_fetchedResult.get(l_ge.makeKey())));
            }
        } finally {
            l_exportedFile.delete();
        }

    }

    @Test
    public void exportLogData_negativeEmptyData() throws LogDataExportToFileException {
        LogData<GenericEntry> l_emptyLogData = new LogData<>();
        File l_shouldBeEmpty = l_emptyLogData.exportLogDataToCSV();
        assertThat("The returned file should not exist", !l_shouldBeEmpty.exists());
    }

}
