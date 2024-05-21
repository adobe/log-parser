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
package com.adobe.campaign.tests.logparser.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class allows for the definition of one rule that allows you to extract
 * one piece of data from a string.
 *
 *
 * Author : gandomi
 *
 */
public class ParseDefinitionEntry {

    private String title;
    private String start;
    private String end;
    private boolean caseSensitive = true;
    private boolean trimQuotes = false;
    private boolean toPreserve = true;

    public ParseDefinitionEntry(String in_title) {
        this.title = in_title;
    }

    public ParseDefinitionEntry() {

    }

    public ParseDefinitionEntry(ParseDefinitionEntry in_oldDefinitionEntry) {
        this.title = in_oldDefinitionEntry.title;
        this.start = in_oldDefinitionEntry.start;
        this.end = in_oldDefinitionEntry.end;
        this.caseSensitive = in_oldDefinitionEntry.caseSensitive;
        this.trimQuotes = in_oldDefinitionEntry.trimQuotes;
        this.toPreserve = in_oldDefinitionEntry.toPreserve;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    /**
     * If the end string is null we will consider the the end to be the end of
     * the line/string
     *
     * Author : gandomi
     *
     * @param end
     *        The character series that signals the end of the value our entry
     *        is looking for. If the end string is null we will consider the the
     *        end to be the end of the line/string
     *
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * Provides the start position of the 'start' string in the given start.
     * This returns -1 if there is no occurence of the start string
     *
     * Author : gandomi
     *
     * @param in_stringValue
     *        A string that is to be parsed using the current
     *        ParseDenfinitionEntry
     * @return the index of the first occurrence of the specified substring, or
     *         -1 if there is no such occurrence.
     *
     */
    public int fetchStartPosition(String in_stringValue) {
        if (isStartStartOfLine()) {
            return 0;
        }

        int l_startLocation = fetchAppliedSensitivity(in_stringValue)
                .indexOf(fetchAppliedSensitivity(this.getStart()));

        if (l_startLocation < 0) {
            return l_startLocation;
        } else {

            int lr_startPosition = l_startLocation + this.getStart().length();

            if (isTrimQuotes()) {
                while (in_stringValue.indexOf('"', lr_startPosition) == lr_startPosition) {
                    lr_startPosition++;
                }
            }

            return lr_startPosition;
        }
    }

    /**
     * Provides the end position of the 'end' string in the given start. This
     * returns -1 if there is no occurence of the start string
     *
     * Author : gandomi
     *
     * @param in_stringValue
     *        A string to look for, and for which we return the last index of
     * @return the index of the first occurrence of the specified substring, or
     *         -1 if there is no such occurrence.
     *
     */
    public int fetchEndPosition(String in_stringValue) {
        if (this.isEndEOL()) {
            return in_stringValue.length();
        }

        int lr_endPosition = fetchAppliedSensitivity(in_stringValue)
                .indexOf(fetchAppliedSensitivity(this.getEnd()), this.fetchStartPosition(in_stringValue));

        if ((lr_endPosition >= 0) && isTrimQuotes()) {
            while (in_stringValue.substring(this.fetchStartPosition(in_stringValue), lr_endPosition)
                    .endsWith("\"")) {
                lr_endPosition--;
            }
        }

        return lr_endPosition;
    }

    /**
     * This method returns the following substring that allows for further
     * parsing.
     *
     * Author : gandomi
     *
     * @param in_logString
     *        The string/log line which we are searching through
     * @return A String that will be parsed by the subsequent
     *         ParseDefinitionEntry
     *
     */
    public String fetchFollowingSubstring(String in_logString) {
        int l_currentEndPosition = this.fetchEndPosition(in_logString);

        if (l_currentEndPosition < 0) {
            throw new IllegalArgumentException("The given string :\n " + in_logString
                    + "\n does not contain the end search element " + this.getEnd() + ".");
        }

        return in_logString.substring(l_currentEndPosition);
    }

    /**
     * When used we assum the parse definition to be the end of the string/line
     *
     * Author : gandomi
     *
     *
     */
    public void setEndEOL() {
        end = null;

    }

    /**
     * This method lets us know if the definition ends at the end of the line
     *
     * Author : gandomi
     *
     * @return true if we expect the string to end with EOL. (I.e. if end is
     *         null)
     *
     */
    @JsonIgnore
    public boolean isEndEOL() {

        return getEnd() == null;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * This method transforms a given string to lowercase if the definition is
     * case sensitive
     *
     * Author : gandomi
     *
     * @param in_string
     *        A give parse string
     * @return The given string in lowercase whenever the definition is case
     *         insesitive, other wise the same String.
     *
     */
    public String fetchAppliedSensitivity(String in_string) {

        return isCaseSensitive() ? in_string : in_string.toLowerCase();
    }

    /**
     * This method lets us know if the definition starts at the beginning of the
     * line
     *
     * Author : gandomi
     *
     * @return true if we expect the string to start at the beginning of the
     *         line. (I.e. if start is null)
     *
     */
    @JsonIgnore
    public boolean isStartStartOfLine() {

        return getStart() == null;
    }

    public void setStartStartOfLine() {
        setStart(null);

    }

    public boolean isTrimQuotes() {
        return trimQuotes;
    }

    public void setTrimQuotes(boolean trimQuotes) {
        this.trimQuotes = trimQuotes;
    }

    public boolean isToPreserve() {
        return toPreserve;
    }

    public void setToPreserve(boolean preserve) {
        this.toPreserve = preserve;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (caseSensitive ? 1231 : 1237);
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + (toPreserve ? 1231 : 1237);
        result = prime * result + (trimQuotes ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ParseDefinitionEntry))
            return false;
        ParseDefinitionEntry other = (ParseDefinitionEntry) obj;
        if (caseSensitive != other.caseSensitive)
            return false;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (start == null) {
            if (other.start != null)
                return false;
        } else if (!start.equals(other.start))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (toPreserve != other.toPreserve)
            return false;
        if (trimQuotes != other.trimQuotes)
            return false;
        return true;
    }

}
