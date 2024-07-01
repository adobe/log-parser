/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import com.adobe.campaign.tests.logparser.exceptions.LogParserSDKDefinitionException;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StringParseFactory {

    protected static final Logger log = LogManager.getLogger();
    public static final String STD_DEFAULT_ENTRY_FILENAME = "no_file";

    private StringParseFactory() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * This method transforms the contents of a list of log file and returns a
     * map of LogEntryResults
     *
     * Author : gandomi
     *
     * @param in_logFiles
     *        A collection of log file paths
     * @param in_parseDefinition
     *        The parsing rules as defined in the class ParseDefinition
     * @param in_classTarget
     *        The target class that will be storing the results
     * @param <T> The type of data (subclass of {@link StdLogEntry}) we want to create and store
     * @param <V> The collection type with which we receive the parameter in_logFiles
     * @return A map of String and a Sub-class of {@link StdLogEntry}
     * @throws StringParseException
     *         When there are logical rules when parsing the given string
     *
     */
    public static <T extends StdLogEntry, V extends Collection<String>> Map<String, T> extractLogEntryMap(
            final V in_logFiles, ParseDefinition in_parseDefinition, Class<T> in_classTarget)
            throws StringParseException {

        if (in_logFiles.isEmpty()) {
            log.warn(
                    "The given list of log files 'in_logFiles' was empty. There will be no results available for the ParseDefinition '{}'",
                    in_parseDefinition.getTitle());
        }

        Map<String, T> lr_entries = new HashMap<>();

        Map<String, Integer> l_foundEntries = new HashMap<>();
        long totalBytesAnalyzed = 0;
        //Fetch File
        for (String l_currentLogFile : in_logFiles) {
            int lt_foundEntryCount = 0;


            int i = 0;
            totalBytesAnalyzed+= new File(l_currentLogFile).length();
            log.info("Parsing file {}", l_currentLogFile);

            try (BufferedReader reader = new BufferedReader(new FileReader(l_currentLogFile))) {
                String lt_nextLine;
                while ((lt_nextLine = reader.readLine()) != null) {

                    log.trace("{}  -  {}", i, lt_nextLine);
                    if (isStringCompliant(lt_nextLine, in_parseDefinition)) {
                        updateEntryMapWithParsedData(l_currentLogFile, lt_nextLine, in_parseDefinition, lr_entries,
                                in_classTarget);
                        lt_foundEntryCount++;
                    } else {
                        log.debug("Skipping line {} - {}", i, lt_nextLine);
                    }
                    i++;
                    l_foundEntries.put(l_currentLogFile, lt_foundEntryCount);
                }
                log.info("Finished scanning {} lines.",i, new File(l_currentLogFile).length());
            } catch (IOException e) {
                log.error("The given file {} could not be found.", l_currentLogFile);
            }
        }

        log.info("RESULT : Entry Report for Parse Definition '{}' per file:", in_parseDefinition.getTitle());
        AtomicInteger l_totalEntries = new AtomicInteger();
        l_foundEntries.forEach((k,v) -> {log.info("Found {} entries in file {}", v, k); l_totalEntries.addAndGet(v);});

        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = totalBytesAnalyzed / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;
        log.info("RESULT : Found {} entries, {} unique keys in {} files, and {}Mb of data.", l_totalEntries, lr_entries.keySet().size(), l_foundEntries.keySet().size(),fileSizeInMB);

        return lr_entries;
    }

    /**
     * This method updates the given entry map by parsing the given log line
     *
     * Author : gandomi
     *
     * @param in_logFile The log file from which the line was extracted
     * @param in_logLine
     *        A string representing a log line
     * @param in_parseDefinition
     *        The ParseDefinition rules for parsing the string
     * @param in_entries
     *        The map of String and StdLogEntries
     * @param in_classTarget
     *        The target class that will be storing the results
     * @throws StringParseException
     *         When there are logical rules when parsing the given string
     *
     */
    static <T extends StdLogEntry> void updateEntryMapWithParsedData(final String in_logFile, final String in_logLine,
            ParseDefinition in_parseDefinition, Map<String, T> in_entries, Class<T> in_classTarget)
            throws StringParseException {
        Map<String, String> lt_lineResult = StringParseFactory.parseString(in_logLine, in_parseDefinition);

        T lt_entry = null;
        try {
            lt_entry = in_classTarget.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new LogParserSDKDefinitionException("Structural Problems whith calling the Default constructor in SDK Parser Class", e);
        } catch (InvocationTargetException e) {
            throw new LogParserSDKDefinitionException("Problems when calling the Default constructor in SDK Parser Class", e);
        } catch (NoSuchMethodException e) {
            throw new LogParserSDKDefinitionException("Missing Default constructor in SDK Parser Class", e);
        }

        lt_entry.setParseDefinition(in_parseDefinition);


        var lt_fileObject = new File(in_logFile != null ? in_logFile : STD_DEFAULT_ENTRY_FILENAME);
        if (in_parseDefinition.isStoreFileName()) {
            lt_entry.setLogFileName(lt_fileObject.getName());
        }

        if (in_parseDefinition.isStoreFilePath()) {
            lt_entry.updatePath(lt_fileObject.exists() ? lt_fileObject.getParentFile().getPath() : STD_DEFAULT_ENTRY_FILENAME);
        }

        lt_entry.setValuesFromMap(lt_lineResult);

        final String lt_currentKey = lt_entry.makeKey();

        if (in_entries.containsKey(lt_currentKey)) {
            in_entries.get(lt_currentKey).incrementUsage();

        } else {
            in_entries.put(lt_currentKey, lt_entry);
        }
    }

    /**
     * This method parses a string given a definition
     *
     * Author : gandomi
     *
     * @param in_logString
     *        A string that is to be parsed
     * @param in_parseDefinition
     *        The parse definition rules for parsing the string
     * @return A Map of strings containing the parse results of one parsed line
     * @throws StringParseException
     *         When there are logical rules when parsing the given string
     *
     */
    public static Map<String, String> parseString(String in_logString, ParseDefinition in_parseDefinition)
            throws StringParseException {

        return parseString(in_logString, in_parseDefinition.getDefinitionEntries());
    }

    /**
     * This method parses a string given a definition
     *
     * Author : gandomi
     *
     * @param in_stringToParse
     *        A string that is to be parsed
     * @param in_parsRule
     *        A single parse definition item
     * @return A Map of strings containing the parse results of one parsed line
     * @throws StringParseException
     *         When there are logical rules when parsing the given string
     *
     */
    protected static Map<String, String> parseString(String in_stringToParse,
            ParseDefinitionEntry in_parsRule) throws StringParseException {

        return parseString(in_stringToParse, Arrays.asList(in_parsRule));
    }

    /**
     * This method parses a string given a definition
     *
     * Author : gandomi
     *
     * @param in_logString
     *        A string that is to be parsed
     * @param in_parsRuleList
     *        A list of ParseDefinitionEntries
     * @return A Map of strings containing the parse results of one parsed line
     * @throws StringParseException
     *         When there are logical rules when parsing the given string
     *
     */
    protected static Map<String, String> parseString(String in_logString,
            List<ParseDefinitionEntry> in_parsRuleList) throws StringParseException {

        Map<String, String> lr_stringParseResult = new HashMap<>();
        String l_currentStringState = in_logString;

        for (ParseDefinitionEntry lt_parseRule : in_parsRuleList) {

            lr_stringParseResult.put(lt_parseRule.getTitle(), fetchValue(l_currentStringState, lt_parseRule));
            l_currentStringState = lt_parseRule.fetchFollowingSubstring(l_currentStringState);
        }

        return lr_stringParseResult;
    }

    /**
     * This method parses a string given a start and end character
     *
     * Author : gandomi
     *
     * @param in_stringValue
     *        A string that is to be parsed
     * @param in_parseDefinition
     *        A parse definition entry to parse the given string
     * @return A string representing the value corresponding to the
     *         ParseDefinition
     * @throws StringParseException
     *         Whenever the start and end markers are not found
     *
     */
    public static String fetchValue(String in_stringValue, ParseDefinitionEntry in_parseDefinition)
            throws StringParseException {

        //Fetch where to start looking from
        final int l_startLocation = in_parseDefinition.fetchStartPosition(in_stringValue);

        final int l_endLocation = in_parseDefinition.fetchEndPosition(in_stringValue);

        if (l_startLocation < 0) {
            throw new StringParseException("Could not find the start location for "
                    + in_parseDefinition.getTitle() + " \n" + in_stringValue + ".");
        }

        if (l_endLocation < 0) {
            throw new StringParseException("Could not find the end location for "
                    + in_parseDefinition.getTitle() + " in string \n" + in_stringValue + ".");
        }

        //Anonymize
        var rawExtraction = in_stringValue.substring(l_startLocation, l_endLocation).trim();
        for (String lt_anonymizer : in_parseDefinition.getAnonymizers()) {
            rawExtraction = anonymizeString(lt_anonymizer.trim(), rawExtraction);
        }
        return rawExtraction;

    }

    /**
     * This method lets us know if the given string is compliant with the given
     * definitions
     *
     * Author : gandomi
     *
     * @param in_logString
     *        A string that is to be parsed
     * @param in_definitionList
     *        A list of parse definitions that will be used to fetch the values
     *        in the given string
     * @return true if the Parse Definition rules can be applied to the given
     *         string
     *
     */
    protected static boolean isStringCompliant(String in_logString,
            List<ParseDefinitionEntry> in_definitionList) {
        String l_workingString = in_logString;
        //For every definition start and end. Check that the index follows
        for (ParseDefinitionEntry lt_parseDefinitionItem : in_definitionList) {

            final int lt_startPosition = lt_parseDefinitionItem.fetchStartPosition(l_workingString);

            if (lt_startPosition < 0) {
                return false;
            }

            final int lt_endPosition = lt_parseDefinitionItem.fetchEndPosition(l_workingString);
            if (lt_endPosition < 0) {
                return false;
            }

            //The delta is only relevant if we are preserving the value
            if ((lt_startPosition >= lt_endPosition) && lt_parseDefinitionItem.isToPreserve()) {
                return false;
            }

            l_workingString = lt_parseDefinitionItem.fetchFollowingSubstring(l_workingString);

        }
        return true;
    }

    /**
     * This method lets us know if the given string is compliant with the given
     * parse Definition
     *
     * Author : gandomi
     *
     * @param in_logString
     *        A string that is to be parsed
     * @param in_parseDefinition
     *        A list of parse definitions that will be used to fetch the values
     *        in the given string
     * @return true if the Parse Definition rules can be applied to the given
     *         string
     *
     */
    public static boolean isStringCompliant(String in_logString, ParseDefinition in_parseDefinition) {
        return isStringCompliant(in_logString, in_parseDefinition.getDefinitionEntries());
    }

    /**
     * This method lets us know if the foundStrinf corresponds to the stored string. The stored string will have escape
     * characters like the log4J FormatMessages I.e. '{}
     *
     * @param in_templateString A stored string reference containing the standard escape chracters '{}
     * @param in_candidateString
     * @return
     */
    public static boolean stringsCorrespond(String in_templateString, String in_candidateString) {
        int currentSpot = -1;
        for (String lt_anonymizer : in_templateString.split("\\{\\}")) {
            int lt_currentLocation = in_candidateString.indexOf(lt_anonymizer, currentSpot + 1);
            if (lt_currentLocation <= currentSpot) {
                return false;
            }
            currentSpot = lt_currentLocation;
        }

        return true;
    }


    /**
     * This method anonymizes a string based on a template string. If the template contains {} the corresponding value in the candidate string will be replaced. We also have the opportuning to ignore certain parts of the string by passing [].
     *
     * Author : gandomi
     *
     * @param in_templateString
     *        A string that is to be parsed
     * @param in_candidateString
     *        A list of parse definitions that will be used to fetch the values
     *        in the given string
     * @return A string that is anonymized based on the template string
     *
     */
    public static String anonymizeString(String in_templateString, String in_candidateString) {
        String lr_string = "";
        int l_replace = in_templateString.indexOf('{');
        l_replace = (l_replace < 0) ? 100000 : l_replace;
        int l_keep = in_templateString.indexOf('[');
        l_keep = (l_keep < 0) ? 100000 : l_keep;

        //If replace is before keep recursively call the function up to the keep
        if (l_replace < l_keep) {

            int candSearchString = Math.min(in_templateString.indexOf('{', l_replace + 1) * -1,
                    in_templateString.indexOf('[', l_replace + 1) * -1) * -1;
            int fromCandidate = in_candidateString.indexOf(
                    (candSearchString < 0) ? in_templateString.substring(l_replace + 2) : in_templateString.substring(
                            l_replace + 2, candSearchString));
            lr_string += in_templateString.substring(0, l_replace) + "{}";
            if (l_replace + 2 < in_templateString.length()) {
                lr_string += anonymizeString(in_templateString.substring(l_replace + 2),
                        in_candidateString.substring(fromCandidate));
            }

        } else if (l_replace > l_keep) {
            int candSearchString = Math.min(in_templateString.indexOf('{', l_keep + 1) * -1,
                    in_templateString.indexOf('[', l_keep + 1) * -1) * -1;
            int fromCandidate = in_candidateString.indexOf(
                    (candSearchString < 0) ? in_templateString.substring(l_keep + 2) : in_templateString.substring(
                            l_keep + 2, candSearchString));

            lr_string += in_candidateString.substring(0, fromCandidate);

            //If keep is before replace recursively call the function up to the replace
            lr_string += anonymizeString(in_templateString.substring(l_keep + 2),
                    in_candidateString.substring(fromCandidate));
        } else {
            //If both are equal we can replace the values
            lr_string += in_candidateString;
        }

        return lr_string;

    }
}
