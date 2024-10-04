/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import com.adobe.campaign.tests.logparser.exceptions.LogDataExportToFileException;
import com.adobe.campaign.tests.logparser.utils.LogParserFileUtils;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doThrow;
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

        Map<String, LogDataComparison<GenericEntry>> l_diff = l_cubeData.compare(l_cubeData2);

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

        Map<String, LogDataComparison<GenericEntry>> l_diff = l_cubeData.compare(l_cubeData2);

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

        Map<String, LogDataComparison<GenericEntry>> l_diff = l_cubeData.compare(l_cubeData2);

        assertThat("We should have the correct number of keys",l_diff.size(), is(1));
        assertThat("We should have the correct key",l_diff.containsKey("12"));
        assertThat("We should have the correct delta",
                l_diff.get("12").getDelta(), Matchers.equalTo(2));
        assertThat("We should have the correct key",
                l_diff.get("12").getDeltaRatio(), Matchers.equalTo(100.0));
        assertThat("We should classify this as removed", l_diff.get("12").getChangeType(), Matchers.equalTo(LogDataComparison.ChangeType.NEW));

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

        Map<String, LogDataComparison<GenericEntry>> l_diff = l_cubeData.compare(l_cubeData2);

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

        Map<String, LogDataComparison<GenericEntry>> l_diff = l_cubeData.compare(l_cubeData2);

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
                Matchers.equalTo(LogDataComparison.ChangeType.NEW));
    }

    @Test
    public void testReportGeneration() {
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

        Map<String, LogDataComparison<GenericEntry>> l_diff = l_cubeData.compare(l_cubeData2);
        File l_file = LogDataFactory.generateDiffReport(l_cubeData, l_cubeData2, Arrays.asList("AAZ"),
                "SimpleDiffReport"
        );

        try {
            assertThat("We should have created a file", l_file.exists());
            assertThat("We should have created a file with the correct name", l_file.getName(),
                    Matchers.equalTo("SimpleDiffReport.html"));
        } finally {
            LogParserFileUtils.cleanFile(l_file);
        }
    }


    @Test(enabled = true)
    public void testReportGeneration_2() {
        ParseDefinition l_definition = fetchSTDDefinition();

        //Create first log data
        GenericEntry l_inputData = new GenericEntry(l_definition);
        l_inputData.getValuesMap().put("AAZ", "12");


        //Create second log data
        GenericEntry l_inputData2 = new GenericEntry(l_definition);
        l_inputData2.getValuesMap().put("AAZ", "13");


        LogDataComparison<GenericEntry> l_diff1 = new LogDataComparison<>(l_inputData,
                LogDataComparison.ChangeType.MODIFIED, 3, 2);

        LogDataComparison<GenericEntry> l_diff2 = new LogDataComparison<>(l_inputData2,
                LogDataComparison.ChangeType.MODIFIED, 2, 3);

        Map<String, LogDataComparison<GenericEntry>> l_diff = Map.of("TXO", l_diff2, "one", l_diff1);

        File l_file = LogDataFactory.generateDiffReport(l_diff, Arrays.asList("AAZ"),
                "SimpleDiffReport"
        );

        try {
            assertThat("We should have created a file", l_file.exists());
            assertThat("We should have created a file with the correct name", l_file.getName(),
                    Matchers.equalTo("SimpleDiffReport.html"));
        } finally {
            LogParserFileUtils.cleanFile(l_file);
        }
    }


    @Test
    public void testReportGenerationException() {
        try (MockedStatic<FileUtils> mockFileUtils = Mockito.mockStatic(FileUtils.class)) {
            mockFileUtils.when(() -> FileUtils.writeStringToFile(Mockito.any(), Mockito.any(), Mockito.anyString()))
                    .thenThrow(new IOException("Duuh"));

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


            Map<String, LogDataComparison<GenericEntry>> l_diff = l_cubeData.compare(l_cubeData2);
            assertThrows(LogDataExportToFileException.class, () -> LogDataFactory.generateDiffReport(l_cubeData, l_cubeData2,
                    Arrays.asList("AAZ"), "SimpleDiffReport"
            ));
        }
    }

    private static ParseDefinition fetchSTDDefinition() {
        ParseDefinition l_definition = new ParseDefinition("tmp");
        final ParseDefinitionEntry l_parseDefinitionEntryKey = new ParseDefinitionEntry("AAZ");
        l_definition.addEntry(l_parseDefinitionEntryKey);
        l_definition.defineKeys(l_parseDefinitionEntryKey);
        return l_definition;
    }
}
