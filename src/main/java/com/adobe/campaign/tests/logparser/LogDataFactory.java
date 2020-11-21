/**
 * 
 */
package com.adobe.campaign.tests.logparser;

import java.util.List;

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
     * containing all the data the log parser finds
     *
     * Author : gandomi
     *
     * @param in_filePathList
     *        A list of file paths containing log/generated data
     * @param in_parseDefinition
     *        A ParseDefinition Object defining the parsing rules
     * @return A LogData Object containing the found entries from the logs
     * @throws StringParseException  When there are logical rules when parsing the given string
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     *
     */
    public static LogData<GenericEntry> generateLogData(List<String> in_filePathList,
            ParseDefinition in_parseDefinition) throws InstantiationException, IllegalAccessException, StringParseException {
    
        return LogDataFactory.generateLogData(in_filePathList, in_parseDefinition, GenericEntry.class);
    }

    /**
     * A factory method for LogData. Given a
     * list of files, and a ParseDefinition, and a LogEntryClass it generates a LogDataObject
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
     * @return A LogData Object containing the found entries from the logs
     * @throws StringParseException When there are logical rules when parsing the given string
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     *
     */
    public static <T extends StdLogEntry> LogData<T> generateLogData(List<String> in_filePathList,
            ParseDefinition in_parseDefinition, Class<T> in_logEntryClass) throws InstantiationException, IllegalAccessException, StringParseException {
        
        return new LogData<>(StringParseFactory.extractLogEntryMap(in_filePathList, in_parseDefinition, in_logEntryClass));
    }

}
