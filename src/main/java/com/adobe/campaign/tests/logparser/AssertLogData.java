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
package com.adobe.campaign.tests.logparser;

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
        } catch (InstantiationException | IllegalAccessException | StringParseException e) {
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
        } catch (InstantiationException | IllegalAccessException | StringParseException e) {

            //e.printStackTrace();
            throw new AssertionError("Caught unexpected exception", e);
        }

    }

}
