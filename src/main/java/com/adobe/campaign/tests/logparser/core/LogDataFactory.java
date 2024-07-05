/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */

package com.adobe.campaign.tests.logparser.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.adobe.campaign.tests.logparser.exceptions.LogDataExportToFileException;
import com.adobe.campaign.tests.logparser.exceptions.ParseDefinitionImportExportException;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;
import com.adobe.campaign.tests.logparser.utils.HTMLReportUtils;
import com.adobe.campaign.tests.logparser.utils.LogParserFileUtils;
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
     * @throws StringParseException
     *         When there are logical rules that fail when parsing the given string
     *
     */
    public static LogData<GenericEntry> generateLogData(List<String> in_filePathList,
            ParseDefinition in_parseDefinition)
            throws StringParseException {

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
     * @throws StringParseException
     *         When there are logical rules when parsing the given string
     *
     */
    public static <T extends StdLogEntry> LogData<T> generateLogData(List<String> in_filePathList,
            ParseDefinition in_parseDefinition, Class<T> in_logEntryClass)
            throws StringParseException {

        return new LogData<>(
                StringParseFactory.extractLogEntryMap(in_filePathList, in_parseDefinition, in_logEntryClass));
    }

    /**
     * A factory method for LogData. By default we create GenricEntries. Given a list of files, and a ParseDefinition,
     * it generates a LogDataObject containing all the data the log parser finds
     * <p>
     * Author : gandomi
     *
     * @param in_filePathList                A list of file paths containing log/generated data
     * @param in_jsonParseDefinitionFilePath The file path of a ParseDefinition
     * @param in_logEntryClass               A log entry class that defines how the found data is to be transformed
     * @param <T>                            The type of entry we want to be generated while parsing logs. The type
     *                                       should be a child of {@link StdLogEntry}
     * @return A LogData Object containing the found entries from the logs
     * @throws ParseDefinitionImportExportException Thrown if there is a problem with the given parseDefinition file
     * @throws StringParseException                 When there are logical rules when parsing the given string
     */
    public static <T extends StdLogEntry> LogData<T> generateLogData(List<String> in_filePathList,
            String in_jsonParseDefinitionFilePath, Class<T> in_logEntryClass) throws
            StringParseException, ParseDefinitionImportExportException {

        return generateLogData(in_filePathList,
                ParseDefinitionFactory.importParseDefinition(in_jsonParseDefinitionFilePath),
                in_logEntryClass);
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
     * @throws StringParseException
     *         When there are logical rules that fail when parsing the given string
     *
     */
    public static LogData<GenericEntry> generateLogData(List<String> in_filePathList,
            String in_jsonParseDefinitionFilePath) throws StringParseException, ParseDefinitionImportExportException {

        return LogDataFactory.generateLogData(in_filePathList, in_jsonParseDefinitionFilePath, GenericEntry.class);
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
     * @throws StringParseException                 When there are logical rules when parsing the given string
     */
    public static LogData<GenericEntry> generateLogData(String in_rootDir, String in_fileFilter,
            ParseDefinition in_parseDefinition)
            throws StringParseException {
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
     * @throws StringParseException                 When there are logical rules when parsing the given string
     */
    public static LogData<GenericEntry> generateLogData(String in_rootDir, String in_fileFilter,
            String in_jsonParseDefinitionFilePath) throws StringParseException, ParseDefinitionImportExportException {

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
        l_foundFilesList.forEach(log::info);
        return l_foundFilesList;
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
     * @param in_logEntryClass               A log entry class that defines how the found data is to be transformed
     * @param <T>                            The type of entry we want to be generated while parsing logs. The type
     *                                       should be a child of {@link StdLogEntry}
     * @return A LogData Object containing the found entries from the logs
     * @throws ParseDefinitionImportExportException Thrown if there is a problem with the given parseDefinition file
     * @throws StringParseException                 When there are logical rules when parsing the given string
     */
    public static <T extends StdLogEntry> LogData<T> generateLogData(String in_rootDir, String in_fileFilter, ParseDefinition in_parseDefinition, Class<T> in_logEntryClass)
            throws StringParseException {
        List<String> l_foundFilesList = findFilePaths(in_rootDir, in_fileFilter);
        return generateLogData(l_foundFilesList, in_parseDefinition, in_logEntryClass);
    }

    /**
     * A factory method that creates an html Report of the differences of two log data
     * @param in_logDataReference The Log data that is used as a referen‡ce base
     * @param in_logDataTarget The log data t be compared with the reference
     * @param in_reportName Name of the export file
     * @return The file that was created
     */
    public static <T extends StdLogEntry> File generateDiffReport(LogData<T> in_logDataReference, LogData<T> in_logDataTarget, String in_reportName, List<String> in_headers) {
        Map<String, LogDataComparison> comparisonReport = in_logDataReference.compare(in_logDataTarget);
        StringBuilder sb = new StringBuilder();
        File l_exportFile = new File(in_reportName + ".html");

        LogParserFileUtils.cleanFile(l_exportFile);

        try {
            sb.append("<!DOCTYPE html>");
            sb.append("<html>");
            sb.append("<head>");
            sb.append(HTMLReportUtils.fetchStyleInlineCSS("src/main/resources/diffTable.css"));
            sb.append("<style>");
            sb.append(new String(Files.readAllBytes(Paths.get("src/main/resources/diffTable.css"))));
            sb.append("</style>");
            sb.append("</head>");
            sb.append("<body>");
            //Creating the overview report
            sb.append(HTMLReportUtils.fetchHeader(1, "Overview"));
            sb.append("Here is an overview of the differences between the two log data sets.");
            sb.append(HTMLReportUtils.fetchTableStartBracket());
            sb.append(HTMLReportUtils.fetchTableHeaders(List.of("Metrics", "#")));
            sb.append("<tbody>");
            sb.append(attachSummaryReportLine("New Errors", comparisonReport.values().stream()
                    .filter(l -> l.getChangeType().equals(LogDataComparison.ChangeType.NEW)).count()));
            sb.append(attachSummaryReportLine("Increased Error Numbers", comparisonReport.values().stream()
                    .filter(l -> l.getChangeType().equals(LogDataComparison.ChangeType.MODIFIED))
                    .filter(c -> c.getDelta() > 0).count()
            ));
            sb.append(attachSummaryReportLine("Removed Errors", comparisonReport.values().stream()
                    .filter(l -> l.getChangeType().equals(LogDataComparison.ChangeType.REMOVED)).count()
            ));
            sb.append(attachSummaryReportLine("Decreased Error Numbers", comparisonReport.values().stream()
                    .filter(l -> l.getChangeType().equals(LogDataComparison.ChangeType.MODIFIED))
                    .filter(c -> c.getDelta() < 0).count()
            ));
            sb.append("</tbody>");
            sb.append("</table>");
            //Creating the Detailed report
            sb.append(HTMLReportUtils.fetchHeader(1, "Detailed"));
            sb.append("Detailed report of the differences between the two log data sets grouped by change type.<p>");
            comparisonReport.values().stream().map(LogDataComparison::getChangeType).distinct().sorted().forEach(l_changeType -> {
                List<LogDataComparison> l_entries = comparisonReport.values().stream().filter(l -> l.getChangeType().equals(l_changeType)).sorted(Comparator.comparing(LogDataComparison::getDelta)).collect(
                        Collectors.toList());
                Collections.reverse(l_entries);
                //l_entries.sort(Comparator.comparing(LogDataComparison::getDelta));
                sb.append(HTMLReportUtils.fetchHeader(3, l_changeType.name()));
                sb.append("<table>");
                sb.append(HTMLReportUtils.fetchTableHeaders(Stream.concat(in_headers.stream(), Stream.of("delta", "deltaRatio")).collect(Collectors.toList())));
                sb.append("<tbody>");
                sb.append("</tr>");
                l_entries.forEach(l -> {
                    sb.append("<tr>");
                    in_headers.forEach(h -> sb.append(HTMLReportUtils.fetchCell_TD(l.getLogEntry().fetchValueMap().get(h))));
                    sb.append(HTMLReportUtils.fetchCell_TD(l.getDelta()));
                    sb.append(HTMLReportUtils.fetchCell_TD(l.getDeltaRatio() +" %"));
                    sb.append("</tr>");
                });
                sb.append("</tbody>");

                sb.append("</table>");
            });
            sb.append("</body>");
            sb.append("</html>");

            FileUtils.writeStringToFile(l_exportFile, sb.toString(), "UTF-8");
        } catch (IOException e) {
            throw new LogDataExportToFileException("We were unable to write to the file "+ l_exportFile.getPath());
        }
        return l_exportFile;
    }

    /**
     * A factory method that adds a line to the summary report
     *
     * @param in_header The header of the summary line
     * @param in_number The number to be added for the report
     * @return A string correspoding to the summary line
     */
    private static String attachSummaryReportLine(String in_header, Object in_number) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<th>" + in_header + "</th>");
        sb.append("<td>");
        sb.append(in_number);
        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

}
