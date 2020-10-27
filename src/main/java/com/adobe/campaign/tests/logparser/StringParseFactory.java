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

public class StringParseFactory {

    protected static final Logger log = LogManager.getLogger();

    /**
     * This method transforms the contents of a list of log file and returns a
     * map of LogEntryResults
     *
     * Author : gandomi
     *
     * @param in_logFiles
     * @param in_parseDefinitionList
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    public static <T extends StdLogEntry, V extends Collection<String>> Map<String, T> fetchLogData(final V in_logFiles,
            List<ParseDefinitionEntry> in_parseDefinitionList, Class<T> classTarget)
            throws InstantiationException, IllegalAccessException {
        Map<String, T> l_entries = new HashMap<String, T>();
        int i = 0;

        //Fetch File
        try {
            for (String l_currentLogFile : in_logFiles) {

                Scanner scanner = new Scanner(new File(l_currentLogFile));

                while (scanner.hasNextLine()) {

                    final String lt_nextLine = scanner.nextLine();
                    //Activate only if the log is not enough. Here we list each line we consider
                    //log.debug("{}  -  {}", i, lt_nextLine);
                    if (isStringCompliant(lt_nextLine, in_parseDefinitionList)) {
                        Map<String, String> lt_lineResult = StringParseFactory.parseString(lt_nextLine,
                                in_parseDefinitionList);

                        T lt_entry = classTarget.newInstance();
                        lt_entry.setValuesFromMap(lt_lineResult,in_parseDefinitionList);
                        //lt_entry.setValuesFromMap(lt_lineResult);

                        final String lt_currentKey = lt_entry.makeKey();
                        
                        if (l_entries.containsKey(lt_currentKey)) {
                            l_entries.get(lt_currentKey).incrementUsage();
                            
                        } else {
                            l_entries.put(lt_currentKey, lt_entry);
                        }

                    } else {
                        log.debug("Skipping line {} - {}", i, lt_nextLine);
                    }
                    i++;

                }
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return l_entries;
    }

    /**
     * This method parses a string given a definition
     *
     * Author : gandomi
     *
     * @param in_stringToParse
     * @param in_parsRule
     * @return
     *
     */
    public static Map<String, String> parseString(String in_stringToParse, ParseDefinitionEntry in_parsRule) {

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
     *
     */
    public static Map<String, String> parseString(String in_logString,
            List<ParseDefinitionEntry> in_parsRuleList) {

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
     *
     */
    public static String fetchValue(String in_stringValue, ParseDefinitionEntry in_parseDefinition) {

        //Fetch where to start looking from
        final int l_startLocation = in_parseDefinition.fetchStartPosition(in_stringValue);

        final int l_endLocation = in_parseDefinition.fetchEndPosition(in_stringValue);

        if (l_startLocation < 0) {
            throw new RuntimeException("Could not find the start location for "
                    + in_parseDefinition.getTitle() + " \n" + in_stringValue + ".");
        }

        if (l_endLocation < 0) {
            throw new RuntimeException("Could not find the end location for " + in_parseDefinition.getTitle()
                    + " in string \n" + in_stringValue + ".");
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
    public static boolean isStringCompliant(String in_logString, List<ParseDefinitionEntry> in_definitionList) {
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

}
