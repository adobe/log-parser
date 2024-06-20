/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertThrows;

/**
 * This class is meant to test the difference between two LogData objects
 */
public class TestDifference {
    @Test
    public void simpleNoDifference() {
        ParseDefinition l_definition = fetchSTDDefinition();

        //Create first log data
        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.getValuesMap().put("AAZ", "12");
        LogData<GenericEntry> l_cubeData = new LogData<>(l_inputData);


        //Create second log data
        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.getValuesMap().put("AAZ", "12");
        LogData<GenericEntry> l_cubeData2 = new LogData<>(l_inputData2);

        Map<String, LogDataComparison> l_diff = l_cubeData.compare(l_cubeData2);

        assertThat("We should have no keys",l_diff.size(), is(0));
    }


    @Test
    public void simpleDifferenceInFrequence() {
        ParseDefinition l_definition = fetchSTDDefinition();

        //Create first log data
        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.getValuesMap().put("AAZ", "12");
        LogData<GenericEntry> l_cubeData = new LogData<>(l_inputData);


        //Create second log data
        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.getValuesMap().put("AAZ", "12");
        LogData<GenericEntry> l_cubeData2 = new LogData<>(l_inputData2);
        l_cubeData2.addEntry(l_inputData2);

        Map<String, LogDataComparison> l_diff = l_cubeData.compare(l_cubeData2);

        assertThat("We should have the correct number of keys",l_diff.size(), is(1));
        assertThat("We should have the correct key",l_diff.containsKey("12"));
        assertThat("We should have the correct key",
                l_diff.get("12").getDelta(), Matchers.equalTo(1));
        assertThat("We should have the correct key",
                l_diff.get("12").getDeltaRatio(), Matchers.equalTo(200.0));
        assertThat("We should classify this as Modified", l_diff.get("12").getChangeType(), Matchers.equalTo(LogDataComparison.ChangeType.MODIFIED));

        assertThat("We should have stored the original logData", l_diff.get("12").getLogEntry(), Matchers.equalTo(l_cubeData2.get("12")));
    }

    @Test
    public void simpleDifferenceEmptyLeftSide() {
        ParseDefinition l_definition = fetchSTDDefinition();

        //Create first log data
        LogData<GenericEntry> l_cubeData = new LogData<>();


        //Create second log data
        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.getValuesMap().put("AAZ", "12");
        LogData<GenericEntry> l_cubeData2 = new LogData<>(l_inputData2);
        l_cubeData2.addEntry(l_inputData2);

        Map<String, LogDataComparison> l_diff = l_cubeData.compare(l_cubeData2);

        assertThat("We should have the correct number of keys",l_diff.size(), is(1));
        assertThat("We should have the correct key",l_diff.containsKey("12"));
        assertThat("We should have the correct delta",
                l_diff.get("12").getDelta(), Matchers.equalTo(2));
        assertThat("We should have the correct key",
                l_diff.get("12").getDeltaRatio(), Matchers.equalTo(100.0));
        assertThat("We should classify this as removed", l_diff.get("12").getChangeType(), Matchers.equalTo(LogDataComparison.ChangeType.ADDED));

        assertThat("We should have stored the original logData", l_diff.get("12").getLogEntry(), Matchers.equalTo(l_cubeData2.get("12")));
    }

    @Test
    public void simpleDifferenceEmptyRightSide() {
        ParseDefinition l_definition = fetchSTDDefinition();

        //Create first log data
        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.getValuesMap().put("AAZ", "12");
        LogData<GenericEntry> l_cubeData = new LogData<>(l_inputData);


        //Create second log data
        LogData<GenericEntry> l_cubeData2 = new LogData<>();

        Map<String, LogDataComparison> l_diff = l_cubeData.compare(l_cubeData2);

        assertThat("We should have the correct number of keys",l_diff.size(), is(1));
        assertThat("We should have the correct key",l_diff.containsKey("12"));
        assertThat("We should have the correct delta",
                l_diff.get("12").getDelta(), Matchers.equalTo(-1));
        assertThat("We should have the correct key",
                l_diff.get("12").getDeltaRatio(), Matchers.equalTo(-100.0));
        assertThat("We should classify this as removed", l_diff.get("12").getChangeType(), Matchers.equalTo(LogDataComparison.ChangeType.REMOVED));

        assertThat("We should have stored the original logData", l_diff.get("12").getLogEntry(), Matchers.equalTo(l_cubeData.get("12")));
    }

    @Test
    public void simpleAddedRemoved() {
        ParseDefinition l_definition = fetchSTDDefinition();

        //Create first log data
        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.getValuesMap().put("AAZ", "12");
        LogData<GenericEntry> l_cubeData = new LogData<>(l_inputData);

        //Create second log data
        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.getValuesMap().put("AAZ", "13");
        LogData<GenericEntry> l_cubeData2 = new LogData<>(l_inputData2);
        l_cubeData2.addEntry(l_inputData2);

        Map<String, LogDataComparison> l_diff = l_cubeData.compare(l_cubeData2);

        assertThat("We should have the correct number of keys", l_diff.size(), is(2));
        assertThat("We should have the correct key", l_diff.keySet(), Matchers.containsInAnyOrder("12", "13"));
        assertThat("We should have the correct delta",
                l_diff.get("12").getDelta(), Matchers.equalTo(-1));
        assertThat("We should have the correct delta",
                l_diff.get("13").getDelta(), Matchers.equalTo(2));
        assertThat("We should have the correct key",
                l_diff.get("12").getDeltaRatio(), Matchers.equalTo(-100.0));
        assertThat("We should have the correct key",
                l_diff.get("13").getDeltaRatio(), Matchers.equalTo(100.0));
        assertThat("We should classify this as removed", l_diff.get("12").getChangeType(),
                Matchers.equalTo(LogDataComparison.ChangeType.REMOVED));
        assertThat("We should classify this as removed", l_diff.get("13").getChangeType(),
                Matchers.equalTo(LogDataComparison.ChangeType.ADDED));
    }

    @Test
    public void simpleAddedRemovedComplex() {
        ParseDefinition l_definition = fetchSTDDefinition();

        //Create first log data
        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.getValuesMap().put("AAZ", "12");
        LogData<GenericEntry> l_cubeData = new LogData<>(l_inputData);
        GenericEntry l_inputData4 = new GenericEntry(l_definition);
        l_inputData4.getValuesMap().put("AAZ", "13");
        l_cubeData.addEntry(l_inputData4);

        //Create second log data
        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.getValuesMap().put("AAZ", "12");
        LogData<GenericEntry> l_cubeData2 = new LogData<>(l_inputData2);
        l_cubeData2.addEntry(l_inputData2);
        GenericEntry l_inputData3 = new GenericEntry(l_definition);
        l_inputData3.getValuesMap().put("AAZ", "14");
        l_cubeData2.addEntry(l_inputData3);

        GenericEntry l_inputData4b = new GenericEntry(l_definition);
        l_inputData4b.getValuesMap().put("AAZ", "13");
        l_cubeData2.addEntry(l_inputData4b);
        l_cubeData2.addEntry(l_inputData4b);
        l_cubeData2.addEntry(l_inputData4b);

        Map<String, LogDataComparison> l_diff = l_cubeData.compare(l_cubeData2);

        File l_file = LogDataFactory.generateDiffReport(l_cubeData,l_cubeData2, "SimpleDiffReport", Arrays.asList("AAZ"));

        assertThat("We should have created a file", l_file.exists());
        assertThat("We should have created a file with the correct name", l_file.getName(), Matchers.equalTo("SimpleDiffReport.html"));

    }




    private static ParseDefinition fetchSTDDefinition() {
        ParseDefinition l_definition = new ParseDefinition("tmp");
        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.defineKeys(l_parseDefinitionEntryKey);
        return l_definition;
    }
}
