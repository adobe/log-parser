/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import com.adobe.campaign.tests.logparser.exceptions.StringParseException;
import org.hamcrest.Matchers;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertThrows;

public class AssertionTests {

    /**
     * Testing that we correctly create a cube
     * <p>
     * Author : gandomi
     */
    @Test
    public void testSimpleAssertion() {

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

        AssertLogData.assertLogContains(l_cubeData, l_parseDefinitionEntryKey, "112");
        AssertLogData.assertLogContains("This should work", l_cubeData, l_parseDefinitionEntryKey, "112");

        AssertLogData.assertLogContains("This should also work", l_cubeData, "AAZ", "112");

        Assert.assertThrows(AssertionError.class,
                () -> AssertLogData.assertLogContains(l_cubeData, l_parseDefinitionEntryKey, "124"));

        try {
            AssertLogData.assertLogContains("But this should not", l_cubeData,
                    l_parseDefinitionEntryKey.getTitle(), "112");
        } catch (AssertionError ae) {
            assertThat("The comment should contain the passed string", ae.getMessage(),
                    Matchers.containsString("But this should not"));
            assertThat("The comment should contain the name of the parseDefinition title", ae.getMessage(),
                    Matchers.containsString(l_parseDefinitionEntryKey.getTitle()));
        }
    }

    /**
     * Testing a possible usecase. This test is a copy of the test {@link LogDataTest#testLogDataFactory()}
     * <p>
     * Author : gandomi
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws StringParseException
     */
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

        AssertLogData.assertLogContains("We should have gound the entry PrepareFromId", Arrays.asList(apacheLogFile),
                l_pDefinition, "verb", "PrepareFromId");
        AssertLogData.assertLogContains(Arrays.asList(apacheLogFile), l_pDefinition, "verb", "PrepareFromId");
    }

    /**
     * Testing a possible usecase. This test is a copy of the test {@link LogDataTest#testLogDataFactory()}. Testing
     * Exception
     * <p>
     * Author : gandomi
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws StringParseException
     */
    @Test
    public void testLogDataFactory_NegativeExceptionThrown()
            throws InstantiationException, IllegalAccessException, StringParseException {
        try (MockedStatic<LogDataFactory> mockedLogFactory = Mockito.mockStatic(LogDataFactory.class)) {

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

            mockedLogFactory.when(() -> LogDataFactory.generateLogData(Arrays.asList(apacheLogFile), l_pDefinition))
                    .thenThrow(new InstantiationException("Duuh"));

            assertThrows(AssertionError.class,
                    () -> AssertLogData.assertLogContains("We should have found the entry PrepareFromId",
                            Arrays.asList(apacheLogFile), l_pDefinition, "verb", "PrepareFromId"));

            assertThrows(AssertionError.class,
                    () -> AssertLogData.assertLogContains(Arrays.asList(apacheLogFile), l_pDefinition, "verb",
                            "PrepareFromId"));
        }
    }

    @Test
    public void testLogDataFactory_NegativeInstatiation() {
        assertThrows(IllegalStateException.class, () -> new AssertLogData());
    }

}
