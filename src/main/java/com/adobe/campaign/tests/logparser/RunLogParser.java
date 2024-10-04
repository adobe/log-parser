/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser;

import com.adobe.campaign.tests.logparser.core.LogData;
import com.adobe.campaign.tests.logparser.core.LogDataFactory;
import com.adobe.campaign.tests.logparser.core.ParseDefinition;
import com.adobe.campaign.tests.logparser.core.ParseDefinitionFactory;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;
import com.adobe.campaign.tests.logparser.utils.RunArguments;

import java.util.Arrays;

public class RunLogParser {
    /**
     * The main method for running the log parser as a tools
     * @param in_args an argument array
     * @throws StringParseException When there are issues during parsing
     */
    public static void main(String[] in_args) throws StringParseException {
        //Print help if requested
        if (Arrays.stream(in_args).anyMatch(RunArguments.HELP::correspondsTo)) {
            System.out.println(RunArguments.fetchHelpText());
            return;
        }

        StringBuilder l_mandatories = new StringBuilder();
        //Check if the mandatory values are there
        RunArguments.getMandatoryCommands().stream().forEach(mc -> {
            if (mc.fetchValue(in_args, null) == null) {
                l_mandatories.append("Mandatory argument ").append(mc.buildTag()).append(" is missing").append("\n");
            }
        });

        if (l_mandatories.length() > 0) {
            System.err.println(l_mandatories);
            System.out.println(RunArguments.fetchHelpText());
            return;
        }

        //Fetch parse definition
        ParseDefinition l_parseDefinition = ParseDefinitionFactory.importParseDefinition(
                RunArguments.PARSE_DEFINITIONS_FILE.fetchValue(in_args));

        //Extract the target SDK class
        Class l_targetSDKClass;
        try {
            l_targetSDKClass = Class.forName(RunArguments.TARGET_SDK_CLASS.fetchValue(in_args));
        } catch (ClassNotFoundException e) {
            System.err.println("The target SDK class " + RunArguments.TARGET_SDK_CLASS.fetchValue(in_args)
                    + " could not be found");
            return;
        }

        //Generate Log data
        LogData l_logData = LogDataFactory.generateLogData(RunArguments.START_DIR.fetchValue(in_args),
                RunArguments.FILTER_LOG_FILES.fetchValue(in_args), l_parseDefinition,
                l_targetSDKClass);

        //Generate Report
        switch (RunArguments.REPORT_FORMAT.fetchValue(in_args).toUpperCase()) {
            case "CSV":
                l_logData.exportLogDataToCSV(RunArguments.REPORT_FILENAME.fetchValue(in_args,
                        l_parseDefinition.fetchEscapedTitle() + "-export.csv"));
                break;
            case "HTML":
                l_logData.exportLogDataToHTML(
                        RunArguments.REPORT_NAME.fetchValue(in_args, l_parseDefinition.getTitle()),
                        RunArguments.REPORT_FILENAME.fetchValue(in_args,
                                l_parseDefinition.fetchEscapedTitle() + "-export.html"));
                break;
            case "JSON":
                l_logData.exportLogDataToJSON(RunArguments.REPORT_FILENAME.fetchValue(in_args,
                        l_parseDefinition.fetchEscapedTitle() + "-export.json"));
                break;
            default:
                System.err.println("The report format " + RunArguments.REPORT_FORMAT.fetchValue(in_args)
                        + " is not supported.");
        }


    }
}
