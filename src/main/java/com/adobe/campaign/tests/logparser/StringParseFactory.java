/**
 * Copyright 2020 Baubak Gandomi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.adobe.campaign.tests.logparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.adobe.campaign.tests.logparser.exceptions.StringParseException;

public class StringParseFactory {

    protected static final Logger log = LogManager.getLogger();

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
     * @param in_parseDefinition
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws StringParseException
     *
     */
    public static <T extends StdLogEntry, V extends Collection<String>> Map<String, T> fetchLogData(
            final V in_logFiles, ParseDefinition in_parseDefinition, Class<T> classTarget)
            throws InstantiationException, IllegalAccessException, StringParseException {
        Map<String, T> l_entries = new HashMap<>();
        int i = 0;

        //Fetch File
        for (String l_currentLogFile : in_logFiles) {

            try (Scanner scanner = new Scanner(new File(l_currentLogFile))) {

                while (scanner.hasNextLine()) {

                    final String lt_nextLine = scanner.nextLine();
                    //Activate only if the log is not enough. Here we list each line we consider
                    //log.debug("{}  -  {}", i, lt_nextLine);
                    if (isStringCompliant(lt_nextLine, in_parseDefinition)) {
                        updateEntryMapWithParsedData(lt_nextLine, in_parseDefinition, l_entries, classTarget);

                    } else {
                        log.debug("Skipping line {} - {}", i, lt_nextLine);
                    }
                    i++;

                }
            } catch (FileNotFoundException e) {
                log.error("The given file {} could not be found.", l_currentLogFile);
            }
        }

        return l_entries;
    }

    /**
     * This method updated the given entry map by parsing the given log line
     *
     * Author : gandomi
     *
     * @param in_logLine
     * @param in_parseDefinition
     * @param in_entries
     * @param in_classTarget
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws StringParseException
     *
     */
    static <T extends StdLogEntry> void updateEntryMapWithParsedData(final String in_logLine,
            ParseDefinition in_parseDefinition, Map<String, T> in_entries, Class<T> in_classTarget)
            throws InstantiationException, IllegalAccessException, StringParseException {
        Map<String, String> lt_lineResult = StringParseFactory.parseString(in_logLine, in_parseDefinition);

        T lt_entry = in_classTarget.newInstance();

        lt_entry.setParseDefinition(in_parseDefinition);
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
     * @param in_parseDefinition
     * @return
     * @throws StringParseException
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
     * @param in_parsRule
     * @return
     * @throws StringParseException
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
     * @param in_parsRuleList
     * @return
     * @throws StringParseException
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
     * @param in_parseDefinition
     * @return
     * @throws StringParseException
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

        return in_stringValue.substring(l_startLocation, l_endLocation).trim();

    }

    /**
     * This method lets us know if the given string is compliant with the given
     * definitions
     *
     * Author : gandomi
     *
     * @param in_logString
     * @param in_definitionList
     * @return
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
     * @param in_parseDefinition
     * @return
     *
     */
    public static boolean isStringCompliant(String in_logString, ParseDefinition in_parseDefinition) {
        return isStringCompliant(in_logString, in_parseDefinition.getDefinitionEntries());
    }

}
