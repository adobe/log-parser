/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import java.util.List;

import com.adobe.campaign.tests.logparser.exceptions.StringParseException;

/**
 * Assertion mechanisms for logs. Using this class you can perform assertions on log data
 */
public class AssertLogData {

    protected AssertLogData() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * An assert that lets us see if the log data contains an entry with a given
     * value for a definition
     * <p>
     * Author : gandomi
     * <p>
     * @param in_logData
     *        A Log data object
     * @param in_parseDefinitionEntry
     *        A Parse Definition entry used for the log entries in the log data
     * @param in_expectedValue
     *        The expected value for the entry
     * @param <T>
     *        The type of entry we want to be generated while parsing logs. The
     *        type should be a child of {@link StdLogEntry}
     *
     */
    public static <T extends StdLogEntry> void assertLogContains(LogData<T> in_logData,
            ParseDefinitionEntry in_parseDefinitionEntry, String in_expectedValue) {

        assertLogContains(in_logData, in_parseDefinitionEntry.getTitle(), in_expectedValue);
    }

    /**
     * An assert that lets us see if the log data contains an entry with a given
     * value for a definition
     * <p>
     * Author : gandomi
     * <p>
     * @param in_comment
     *        A comment that is presented whenever the assertion fails
     * @param in_logData
     *        A Log data object
     * @param in_parseDefinitionEntry
     *        A Parse Definition entry used for the log entries in the log data
     * @param in_expectedValue
     *        The expected value for the entry
     * @param <T>
     *        The type of entry we want to be generated while parsing logs. The
     *        type should be a child of {@link StdLogEntry}
     *
     */
    public static <T extends StdLogEntry> void assertLogContains(String in_comment, LogData<T> in_logData,
            ParseDefinitionEntry in_parseDefinitionEntry, String in_expectedValue) {

        assertLogContains(in_comment, in_logData, in_parseDefinitionEntry.getTitle(), in_expectedValue);
    }

    /**
     * An assert that lets us see if the log data contains an entry with a given
     * value for a definition
     * <p>
     * Author : gandomi
     * <p>
     * @param in_logData
     *        A Log data object
     * @param in_parseDefinitionEntryTitle
     *        A Parse Definition entry used for the log entries in the log data
     * @param in_expectedValue
     *        The expected value for the entry
     * @param <T>
     *        The type of the LogData. The type should be a child of
     *        {@link StdLogEntry}
     */
    public static <T extends StdLogEntry> void assertLogContains(LogData<T> in_logData,
            String in_parseDefinitionEntryTitle, String in_expectedValue) {

        final String l_assertionErrorComment = "Expected the ParseDefinitionEntry "
                + in_parseDefinitionEntryTitle + " to be present in the log data.";

        assertLogContains(l_assertionErrorComment, in_logData, in_parseDefinitionEntryTitle,
                in_expectedValue);
    }

    /**
     * An assert that lets us see if the log data contains an entry with a given
     * value for a definition
     * <p>
     * Author : gandomi
     * <p>
     * @param in_comment
     *        A comment that is presented whenever the assertion fails
     * @param in_logData
     *        A Log data object
     * @param in_parseDefinitionEntryTitle
     *        A Parse Definition entry used for the log entries in the log data
     * @param in_expectedValue
     *        The expected value for the entry
     * @param <T>
     *        The type of the LogData. The type should be a child of
     *        {@link StdLogEntry}
     */
    public static <T extends StdLogEntry> void assertLogContains(String in_comment, LogData<T> in_logData,
            String in_parseDefinitionEntryTitle, String in_expectedValue) {

        if (!in_logData.isEntryPresent(in_parseDefinitionEntryTitle, in_expectedValue)) {
            throw new AssertionError(in_comment);
        }
    }

    /**
     * This is an assertion at a file level. Given a set of log files it will
     * let you know if a given entry can be found
     * <p>
     * Author : gandomi
     * <p>
     * @param in_filePathList
     *        A list of file paths containing log/generated data
     * @param in_parseDefinition
     *        A ParseDefinition Object defining the parsing rules
     * @param in_parseDefinitionEntryTitle
     *        A Parse Definition entry used for the log entries in the log data
     * @param in_expectedValue
     *        The expected value for the entry
     * 
     */
    public static void assertLogContains(List<String> in_filePathList, ParseDefinition in_parseDefinition,
            String in_parseDefinitionEntryTitle, String in_expectedValue) {
        try {

            assertLogContains(LogDataFactory.generateLogData(in_filePathList, in_parseDefinition),
                    in_parseDefinitionEntryTitle, in_expectedValue);
        } catch (StringParseException e) {
            throw new AssertionError("Caught unexpected exception", e);
        }

    }

    /**
     * This is an assertion at a file level. Given a set of log files it will
     * let you know if a given entry can be found
     *
     * Author : gandomi
     *
     * @param in_comment
     *        A comment that is presented whenever the assertion fails
     * @param in_filePathList
     *        A list of file paths containing log/generated data
     * @param in_parseDefinition
     *        A ParseDefinition Object defining the parsing rules
     * @param in_parseDefinitionEntryTitle
     *        A Parse Definition entry used for the log entries in the log data
     * @param in_expectedValue
     *        The expected value for the entry
     *        
     */
    public static void assertLogContains(String in_comment, List<String> in_filePathList,
            ParseDefinition in_parseDefinition, String in_parseDefinitionEntryTitle, String in_expectedValue) {

        try {

            assertLogContains(in_comment, LogDataFactory.generateLogData(in_filePathList, in_parseDefinition),
                    in_parseDefinitionEntryTitle, in_expectedValue);
        } catch (StringParseException e) {

            //e.printStackTrace();
            throw new AssertionError("Caught unexpected exception", e);
        }

    }

}
