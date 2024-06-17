/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import com.adobe.campaign.tests.logparser.data.SDKCaseBadDefConstructor;
import com.adobe.campaign.tests.logparser.data.SDKCaseNoDefConstructor;
import com.adobe.campaign.tests.logparser.data.SDKCasePrivateDefConstructor;
import com.adobe.campaign.tests.logparser.data.SDKCaseSTD;
import com.adobe.campaign.tests.logparser.exceptions.LogDataExportToFileException;
import com.adobe.campaign.tests.logparser.exceptions.LogParserSDKDefinitionException;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SDKTests {

    private static ParseDefinition getTestParseDefinition() {
        ParseDefinitionEntry l_timeStamp = new ParseDefinitionEntry();
        l_timeStamp.setTitle("timeStamp");
        l_timeStamp.setStartStartOfLine();
        l_timeStamp.setEnd("\t");

        ParseDefinitionEntry l_throwablPart1 = new ParseDefinitionEntry();
        l_throwablPart1.setTitle("throwableMarker");
        l_throwablPart1.setStart("\terror");
        l_throwablPart1.setEnd("\t");
        l_throwablPart1.setToPreserve(false);

        ParseDefinitionEntry l_errorContent = new ParseDefinitionEntry();
        l_errorContent.setTitle("errorContent");
        l_errorContent.setStart("log\t");
        l_errorContent.setEnd(null);

        ParseDefinition l_pDefinition = new ParseDefinition("My Logs");
        l_pDefinition.addEntry(l_timeStamp);
        l_pDefinition.addEntry(l_throwablPart1);
        l_pDefinition.addEntry(l_errorContent);
        return l_pDefinition;
    }

    @Test
    public void testSimpleLogACC_SDK()
            throws StringParseException, LogDataExportToFileException {

        ParseDefinition l_pDefinition = getTestParseDefinition();
        l_pDefinition.setStoreFileName(true);

        String l_file = "src/test/resources/sdk/useCase1.log";

        LogData<SDKCaseSTD> l_entries = LogDataFactory.generateLogData(Arrays.asList(l_file), l_pDefinition,
                SDKCaseSTD.class);
        assertThat("We should have a correct number of errors", l_entries.getEntries().size(), is(equalTo(14)));
        AssertLogData.assertLogContains(l_entries, "errorMessage",
                "The HTTP query returned a 'Internal Server Error' type error (500) (iRc=16384)");

        assertThat("We should have a file name", l_entries.getEntries().values().iterator().next().getFileName(),
                is(equalTo("useCase1.log")));

        //l_entries.exportLogDataToCSV(l_entries.getEntries().values().iterator().next().fetchHeaders(),
        //        "./z.csv");
    }

    @Test
    public void testSimpleLogACC_SDK_nested()
            throws StringParseException, LogDataExportToFileException {

        ParseDefinition l_pDefinition = getTestParseDefinition();
        l_pDefinition.setStoreFileName(true);

        String l_file = "src/test/resources/sdk/";

        LogData<SDKCaseSTD> l_entries = LogDataFactory.generateLogData(l_file, "*.log", l_pDefinition,
                SDKCaseSTD.class);
        assertThat("We should have a correct number of errors", l_entries.getEntries().size(), is(equalTo(14)));
        AssertLogData.assertLogContains(l_entries, "errorMessage",
                "The HTTP query returned a 'Internal Server Error' type error (500) (iRc=16384)");

        assertThat("We should have a file name", l_entries.getEntries().values().iterator().next().getFileName(),
                is(equalTo("useCase1.log")));

    }

    @Test
    public void testSimpleLogACC_SDK_negativeNoDefaultConstructor() {

        ParseDefinition l_pDefinition = getTestParseDefinition();
        l_pDefinition.setStoreFileName(true);

        String l_file = "src/test/resources/sdk/useCase1.log";

        Assert.assertThrows(LogParserSDKDefinitionException.class,
                () -> LogDataFactory.generateLogData(Arrays.asList(l_file), l_pDefinition,
                        SDKCaseNoDefConstructor.class));

    }

    @Test
    public void testSimpleLogACC_SDK_negativeBadConstructor() {

        ParseDefinition l_pDefinition = getTestParseDefinition();
        l_pDefinition.setStoreFileName(true);

        String l_file = "src/test/resources/sdk/useCase1.log";

        Assert.assertThrows(LogParserSDKDefinitionException.class,
                () -> LogDataFactory.generateLogData(Arrays.asList(l_file), l_pDefinition,
                        SDKCaseBadDefConstructor.class));

    }

    @Test
    public void testSimpleLogACC_SDK_negativePrivateConstructor() {

        ParseDefinition l_pDefinition = getTestParseDefinition();
        l_pDefinition.setStoreFileName(true);

        String l_file = "src/test/resources/sdk/useCase1.log";

        Assert.assertThrows(LogParserSDKDefinitionException.class,
                () -> LogDataFactory.generateLogData(Arrays.asList(l_file), l_pDefinition,
                        SDKCasePrivateDefConstructor.class));

    }


    @Test(enabled = false)
    public void testAllLogsACC_SDK()
            throws StringParseException, LogDataExportToFileException {

        ParseDefinition l_pDefinition = getTestParseDefinition();
        l_pDefinition.setStoreFileName(true);

        String l_file = "/Users/gandomi/Downloads/archive_3/";

        LogData<SDKCaseSTD> l_entries = LogDataFactory.generateLogData(l_file, "*.log",l_pDefinition,
                SDKCaseSTD.class);


       // assertThat("We should have a correct number of errors", l_entries.getEntries().size(), is(equalTo(14)));
     //   AssertLogData.assertLogContains(l_entries, "errorMessage",
     //           "The HTTP query returned a 'Internal Server Error' type error (500) (iRc=16384)");

        //l_entries.exportLogDataToCSV(l_entries.getEntries().values().iterator().next().fetchHeaders(),
        //        "./z.csv");

        l_entries.exportLogDataToCSV();
    }
}
