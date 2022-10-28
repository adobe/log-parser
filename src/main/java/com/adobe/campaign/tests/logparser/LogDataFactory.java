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
/**
 * 
 */
package com.adobe.campaign.tests.logparser;

import java.util.List;

import com.adobe.campaign.tests.logparser.exceptions.ParseDefinitionImportExportException;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;

/**
 * Factory class for creating log data
 *
 * Author : gandomi
 *
 */
public class LogDataFactory {

    protected LogDataFactory() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * A factory method for LogData. By default we create GenricEntries. Given a
     * list of files, and a ParseDefinition, it generates a LogDataObject
     * containing all the data the log parser finds.
     *
     * Author : gandomi
     *
     * @param in_filePathList
     *        A list of file paths containing log/generated data
     * @param in_parseDefinition
     *        A ParseDefinition Object defining the parsing rules
     * @return A LogData Object containing the found entries from the logs
     * @throws InstantiationException
     *         if this {@code Class} represents an abstract class, an interface,
     *         an array class, a primitive type, or void; or if the class has no
     *         nullary constructor; or if the instantiation fails for some other
     *         reason.
     * @throws IllegalAccessException
     *         if the class or its nullary constructor is not accessible.
     * @throws StringParseException
     *         When there are logical rules when parsing the given string
     *
     */
    public static LogData<GenericEntry> generateLogData(List<String> in_filePathList,
            ParseDefinition in_parseDefinition)
            throws InstantiationException, IllegalAccessException, StringParseException {

        return LogDataFactory.generateLogData(in_filePathList, in_parseDefinition, GenericEntry.class);
    }

    /**
     * A factory method for LogData. Given a list of files, and a
     * ParseDefinition, and a LogEntryClass it generates a LogDataObject
     * containing all the data the log parser finds
     *
     * Author : gandomi
     *
     * @param in_filePathList
     *        A list of file paths containing log/generated data
     * @param in_parseDefinition
     *        A ParseDefinition Object defining the parsing rules
     * @param in_logEntryClass
     *        A log entry class that defines how the found data is to be
     *        transformed
     * @param <T>
     *        The type of entry we want to be generated while parsing logs. The
     *        type should be a child of {@link StdLogEntry}
     * @return A LogData Object containing the found entries from the logs
     * @throws InstantiationException
     *         if this {@code Class} represents an abstract class, an interface,
     *         an array class, a primitive type, or void; or if the class has no
     *         nullary constructor; or if the instantiation fails for some other
     *         reason.
     * @throws IllegalAccessException
     *         if the class or its nullary constructor is not accessible.
     * @throws StringParseException
     *         When there are logical rules when parsing the given string
     *
     */
    public static <T extends StdLogEntry> LogData<T> generateLogData(List<String> in_filePathList,
            ParseDefinition in_parseDefinition, Class<T> in_logEntryClass)
            throws InstantiationException, IllegalAccessException, StringParseException {

        return new LogData<>(
                StringParseFactory.extractLogEntryMap(in_filePathList, in_parseDefinition, in_logEntryClass));
    }

    /**
     * A factory method for LogData. By default we create GenricEntries. Given a
     * list of files, and a ParseDefinition, it generates a LogDataObject
     * containing all the data the log parser finds
     *
     * Author : gandomi
     *
     * @param in_filePathList
     *        A list of file paths containing log/generated data
     * @param in_jsonParseDefinitionFilePath
     *        The file path of a ParseDefinition
     * @param in_logEntryClass
     *        A log entry class that defines how the found data is to be
     *        transformed
     * @param <T>
     *        The type of entry we want to be generated while parsing logs. The
     *        type should be a child of {@link StdLogEntry}
     * @return A LogData Object containing the found entries from the logs
     * @throws ParseDefinitionImportExportException
     *         Thrown if there is a problem with the given parseDefinition file
     * @throws InstantiationException
     *         if this {@code Class} represents an abstract class, an interface,
     *         an array class, a primitive type, or void; or if the class has no
     *         nullary constructor; or if the instantiation fails for some other
     *         reason.
     * @throws IllegalAccessException
     *         if the class or its nullary constructor is not accessible.
     * @throws StringParseException
     *         When there are logical rules when parsing the given string
     *
     */
    public static <T extends StdLogEntry> LogData<GenericEntry> generateLogData(List<String> in_filePathList,
            String in_jsonParseDefinitionFilePath, Class<T> in_logEntryClass) throws InstantiationException,
            IllegalAccessException, StringParseException, ParseDefinitionImportExportException {

        return generateLogData(in_filePathList,
                ParseDefinitionFactory.importParseDefinition(in_jsonParseDefinitionFilePath),
                GenericEntry.class);
    }

    /**
     * A factory method for LogData. By default we create GenricEntries. Given a
     * list of files, and a ParseDefinition, it generates a LogDataObject
     * containing all the data the log parser finds
     *
     * Author : gandomi
     *
     * @param in_filePathList
     *        A list of file paths containing log/generated data
     * @param in_jsonParseDefinitionFilePath
     *        The file path of a ParseDefinition
     * @return A LogData Object containing the found entries from the logs
     * @throws ParseDefinitionImportExportException
     *         Thrown if there is a problem with the given parseDefinition file
     * @throws InstantiationException
     *         if this {@code Class} represents an abstract class, an interface,
     *         an array class, a primitive type, or void; or if the class has no
     *         nullary constructor; or if the instantiation fails for some other
     *         reason.
     * @throws IllegalAccessException
     *         if the class or its nullary constructor is not accessible.
     * @throws StringParseException
     *         When there are logical rules when parsing the given string
     *
     */
    public static LogData<GenericEntry> generateLogData(List<String> in_filePathList,
            String in_jsonParseDefinitionFilePath) throws InstantiationException, IllegalAccessException,
            StringParseException, ParseDefinitionImportExportException {

        return LogDataFactory.generateLogData(in_filePathList, in_jsonParseDefinitionFilePath,
                GenericEntry.class);
    }

}
