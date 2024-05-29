/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
/**
 * 
 */
package com.adobe.campaign.tests.logparser.core;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.adobe.campaign.tests.logparser.exceptions.ParseDefinitionImportExportException;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory class for creating log data
 *
 * Author : gandomi
 *
 */
public class LogDataFactory {
    protected static Logger log = LogManager.getLogger();

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

    /**
     * A factory method for LogData. By default we create GenricEntries. Given a root directory path and a wildcard for
     * finding files, it generates a LogDataObject containing all the data the log parser finds in the files matching
     * the search query
     * <p>
     * Author : gandomi
     *
     * @param in_rootDir         A list of file paths containing log/generated data
     * @param in_fileFilter      A wildcard to be used for filtering the files
     * @param in_parseDefinition A ParseDefinition Object defining the parsing rules
     * @return A LogData Object containing the found entries from the logs
     * @throws ParseDefinitionImportExportException Thrown if there is a problem with the given parseDefinition file
     * @throws InstantiationException               if this {@code Class} represents an abstract class, an interface, an
     *                                              array class, a primitive type, or void; or if the class has no
     *                                              nullary constructor; or if the instantiation fails for some other
     *                                              reason.
     * @throws IllegalAccessException               if the class or its nullary constructor is not accessible.
     * @throws StringParseException                 When there are logical rules when parsing the given string
     */
    public static LogData<GenericEntry> generateLogData(String in_rootDir, String in_fileFilter,
            ParseDefinition in_parseDefinition)
            throws StringParseException, InstantiationException, IllegalAccessException {
        List<String> l_foundFilesList = findFilePaths(in_rootDir, in_fileFilter);
        return generateLogData(l_foundFilesList, in_parseDefinition);

    }


    /**
     * A factory method for LogData. By default we create GenricEntries. Given a root directory path and a wildcard for
     * finding files, it generates a LogDataObject containing all the data the log parser finds in the files matching
     * the search query defined as a json
     * <p>
     * Author : gandomi
     *
     * @param in_rootDir                     A starting directory to start our file search
     * @param in_fileFilter                  A wild card pattern to start our searches
     * @param in_jsonParseDefinitionFilePath The file path of a ParseDefinition
     * @return A LogData Object containing the found entries from the logs
     * @throws ParseDefinitionImportExportException Thrown if there is a problem with the given parseDefinition file
     * @throws InstantiationException               if this {@code Class} represents an abstract class, an interface, an
     *                                              array class, a primitive type, or void; or if the class has no
     *                                              nullary constructor; or if the instantiation fails for some other
     *                                              reason.
     * @throws IllegalAccessException               if the class or its nullary constructor is not accessible.
     * @throws StringParseException                 When there are logical rules when parsing the given string
     */
    public static LogData<GenericEntry> generateLogData(String in_rootDir, String in_fileFilter,
            String in_jsonParseDefinitionFilePath) throws InstantiationException, IllegalAccessException,
            StringParseException, ParseDefinitionImportExportException {

        return LogDataFactory.generateLogData(in_rootDir, in_fileFilter,
                ParseDefinitionFactory.importParseDefinition(in_jsonParseDefinitionFilePath));
    }

    /**
     * Given a root directory and a filter, we return a list of files that correspond to our search mechanism
     *
     * @param in_rootDir    A starting directory to start our file search
     * @param in_fileFilter A wild card pattern to start our searches
     * @return a list of file paths that can be used for
     */
    public static List<String> findFilePaths(String in_rootDir, String in_fileFilter) {
        File l_rootDir = new File(in_rootDir);

        if (!l_rootDir.exists()) {
            throw new IllegalArgumentException("The given root path directory " + in_rootDir + " does not exist.");
        }

        if (!l_rootDir.isDirectory()) {
            throw new IllegalArgumentException("The given root path " + in_rootDir + " is not a directory.");
        }

        Iterator<File> l_foundFilesIterator = FileUtils.iterateFiles(l_rootDir,WildcardFileFilter.builder().setWildcards(in_fileFilter).get(), TrueFileFilter.INSTANCE);

        FileFilter fileFilter = WildcardFileFilter.builder().setWildcards(in_fileFilter).get();

        List<String> l_foundFilesList = new ArrayList<>();
        l_foundFilesIterator.forEachRemaining(f -> l_foundFilesList.add(f.getAbsolutePath()));
        log.info("Searching within the {} matching files :", l_foundFilesList.size());
        l_foundFilesList.stream().forEach(log::info);
        return l_foundFilesList;
    }
}
