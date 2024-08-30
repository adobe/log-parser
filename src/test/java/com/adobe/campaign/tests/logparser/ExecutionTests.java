/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser;

import com.adobe.campaign.tests.logparser.core.ParseDefinition;
import com.adobe.campaign.tests.logparser.core.ParseDefinitionFactory;
import com.adobe.campaign.tests.logparser.core.StdLogEntry;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;
import com.adobe.campaign.tests.logparser.utils.CSVManager;
import com.adobe.campaign.tests.logparser.utils.LogParserFileUtils;
import com.adobe.campaign.tests.logparser.utils.RunArguments;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for testing main
 */
public class ExecutionTests {

    //Related to LogDataTest#testLogDataFactoryWithJSONFileForParseDefinitionAndSearchFile
    @Test
    public void testSTDMain_printHTML() throws StringParseException {
        String l_rootPath = "src/test/resources/nestedDirs/";
        String l_fileFilter = "simple*.log";

        final String l_jsonPath = "src/test/resources/parseDefinitions/simpleParseDefinitionLogDataFactory.json";

        ParseDefinition l_parseDefinition = ParseDefinitionFactory.importParseDefinition(l_jsonPath);

        String[] l_args = { RunArguments.START_DIR.buildArgument(l_rootPath),
                RunArguments.FILTER_LOG_FILES.buildArgument(l_fileFilter),
                RunArguments.PARSE_DEFINITIONS_FILE.buildArgument(l_jsonPath) };

        RunLogParser.main(l_args);

        File l_report = new File(LogParserFileUtils.LOG_PARSER_EXPORTS,
                l_parseDefinition.fetchEscapedTitle() + "-export.html");
        try {

            assertThat("We should have generated a file", l_report.exists());
            assertThat("The file should not be empty", l_report.length(), Matchers.greaterThan(0L));
        } finally {
            l_report.delete();
        }

    }

    @Test
    public void testSTDMain_printCSV() throws IOException, StringParseException {
        String l_rootPath = "src/test/resources/nestedDirs/";
        String l_fileFilter = "simple*.log";

        final String l_jsonPath = "src/test/resources/parseDefinitions/simpleParseDefinitionLogDataFactory.json";

        ParseDefinition l_parseDefinition = ParseDefinitionFactory.importParseDefinition(l_jsonPath);

        String[] l_args = { RunArguments.START_DIR.buildArgument(l_rootPath),
                RunArguments.FILTER_LOG_FILES.buildArgument(l_fileFilter),
                RunArguments.PARSE_DEFINITIONS_FILE.buildArgument(l_jsonPath),
                RunArguments.REPORT_FORMAT.buildArgument("CSV") };

        RunLogParser.main(l_args);

        File l_exportedFile = new File(LogParserFileUtils.LOG_PARSER_EXPORTS,
                l_parseDefinition.fetchEscapedTitle() + "-export.csv");

        assertThat("We successfully created the file", l_exportedFile, notNullValue());
        assertThat("We successfully created the file", l_exportedFile.exists());
        assertThat("We successfully created the file correctly", l_exportedFile.isFile());
        try {
            assertThat("We successfully created the file correctly", l_exportedFile.getName(),
                    Matchers.endsWith(l_parseDefinition.fetchEscapedTitle() + "-export.csv"));

            Map<String, List<String>> l_fetchedResult = CSVManager.fetchCSVToMapList(StdLogEntry.STD_DATA_KEY,
                    l_exportedFile);

            assertThat("We should have fetched the data", l_fetchedResult, notNullValue());
            assertThat("We should have fetched the data", l_fetchedResult.size(), Matchers.greaterThan(0));
        } finally {
            l_exportedFile.delete();
        }
    }

    @Test
    public void testSTDMain_printCSV_empty() throws IOException, StringParseException {
        String l_rootPath = "src/test/resources/logTests/acc/";

        final String l_jsonPath = "src/test/resources/parseDefinitions/simpleParseDefinitionLogDataFactory.json";

        ParseDefinition l_parseDefinition = ParseDefinitionFactory.importParseDefinition(l_jsonPath);

        String[] l_args = { RunArguments.START_DIR.buildArgument(l_rootPath),
                RunArguments.PARSE_DEFINITIONS_FILE.buildArgument(l_jsonPath),
                RunArguments.REPORT_FORMAT.buildArgument("CSV") };

        RunLogParser.main(l_args);

        File l_exportedFile = new File(LogParserFileUtils.LOG_PARSER_EXPORTS,
                l_parseDefinition.fetchEscapedTitle() + "-export.csv");

        assertThat("We should not create a file", !l_exportedFile.exists());

    }

    //An unexpected format
    @Test
    public void testSTDMain_printNonExistant() throws StringParseException {
        String l_rootPath = "src/test/resources/nestedDirs/";
        String l_fileFilter = "simple*.log";

        final String l_jsonPath = "src/test/resources/parseDefinitions/simpleParseDefinitionLogDataFactory.json";

        ParseDefinition l_parseDefinition = ParseDefinitionFactory.importParseDefinition(l_jsonPath);

        String[] l_args = { RunArguments.START_DIR.buildArgument(l_rootPath),
                RunArguments.FILTER_LOG_FILES.buildArgument(l_fileFilter),
                RunArguments.PARSE_DEFINITIONS_FILE.buildArgument(l_jsonPath),
                RunArguments.REPORT_FORMAT.buildArgument("NotAFormat") };

        RunLogParser.main(l_args);

        File l_report = new File(LogParserFileUtils.LOG_PARSER_EXPORTS,
                l_parseDefinition.fetchEscapedTitle() + "-export.html");
        try {

            assertThat("We should not have generated a file", !l_report.exists());
        } finally {
            l_report.delete();
        }

    }

    @Test
    public void testHelp() throws StringParseException {

        String[] l_args = { RunArguments.HELP.buildTag() };

        RunLogParser.main(l_args);
    }

    @Test
    public void testNonMandatory() throws StringParseException {

        String[] l_args = {  };

        RunLogParser.main(l_args);
    }

    //A non existing class
    @Test
    public void testBadClass() throws StringParseException {
        String l_rootPath = "src/test/resources/nestedDirs/";
        String l_fileFilter = "simple*.log";

        final String l_jsonPath = "src/test/resources/parseDefinitions/simpleParseDefinitionLogDataFactory.json";

        ParseDefinition l_parseDefinition = ParseDefinitionFactory.importParseDefinition(l_jsonPath);

        String[] l_args = { RunArguments.START_DIR.buildArgument(l_rootPath),
                RunArguments.FILTER_LOG_FILES.buildArgument(l_fileFilter),
                RunArguments.PARSE_DEFINITIONS_FILE.buildArgument(l_jsonPath),
                RunArguments.TARGET_SDK_CLASS.buildArgument("com.adobe.campaign.tests.logparser.core.NonExistant") };

        RunLogParser.main(l_args);

        File l_report = new File(LogParserFileUtils.LOG_PARSER_EXPORTS,
                l_parseDefinition.fetchEscapedTitle() + "-export.html");
        try {

            assertThat("We should not have generated a file", !l_report.exists());
        } finally {
            l_report.delete();
        }

    }


    //Exporting using an SDK class
    //Related to com.adobe.campaign.tests.logparser.core.SDKTests.testSimpleLogACC_SDK
    @Test
    public void testSTDMain_SDK_printHTML() throws StringParseException {
        String l_rootPath = "src/test/resources/sdk";

        final String l_jsonPath = "src/test/resources/parseDefinitions/parseDefinitionSDK.json";

        ParseDefinition l_parseDefinition = ParseDefinitionFactory.importParseDefinition(l_jsonPath);

        String[] l_args = { RunArguments.START_DIR.buildArgument(l_rootPath),
                RunArguments.PARSE_DEFINITIONS_FILE.buildArgument(l_jsonPath),
                RunArguments.TARGET_SDK_CLASS.buildArgument("com.adobe.campaign.tests.logparser.data.SDKCaseSTD"),
                RunArguments.REPORT_FILENAME.buildArgument("SDKCaseSTD-export.html") };

        RunLogParser.main(l_args);

        File l_report = new File(LogParserFileUtils.LOG_PARSER_EXPORTS,
                "SDKCaseSTD-export.html");
        try {

            assertThat("We should have generated a file", l_report.exists());
            assertThat("The file should not be empty", l_report.length(), Matchers.greaterThan(0L));
        } finally {
            l_report.delete();
        }

    }
}
