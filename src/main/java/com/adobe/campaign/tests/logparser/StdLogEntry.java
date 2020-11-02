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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

/**
 * Abstract class for multiple definitions
 *
 *
 * Author : gandomi
 *
 */
public abstract class StdLogEntry {

    private Integer frequence = 1;
    private ParseDefinition parseDefinition;

    Map<String, Object> valuesMap = new HashMap<>();

    public abstract String makeKey();

    public StdLogEntry(ParseDefinition in_definition) {
        this.parseDefinition = in_definition;
    }

    public StdLogEntry() {
        parseDefinition = new ParseDefinition("Created By Default");
    }

    /**
     * Fetches a print out for listing purposed. It uses the header list as an
     * index to fetch the correct order of the values
     *
     * Author : gandomi
     *
     * @returns a String for the print out
     *
     */
    public String fetchPrintOut() {
        List<String> l_printOutList = new ArrayList<>();

        final Map<String, Object> l_valueMap = this.fetchValueMap();

        for (String lt_header : this.fetchHeaders()) {

            l_printOutList.add(l_valueMap.get(lt_header).toString());
        }
        return StringUtils.join(l_printOutList, this.getParseDefinition().getPrintOutPadding());
    }

    /**
     * Fetches the headers that you have defined for the log class. You need to
     * be careful that the headers you have defined have been used in storing
     * the values.
     *
     * Author : gandomi
     *
     * @return a list of header names.
     *
     */
    public abstract List<String> fetchHeaders();

    /**
     * Returns a set of objects you have defined for your log class. When using Genric Object no changes are made to it.
     *
     * Author : gandomi
     *
     * @return
     *
     */
    public abstract Map<String, Object> fetchValueMap();

    /**
     * Increments the frequence
     *
     * Author : gandomi
     *
     *
     */
    protected void incrementUsage() {
        addFrequence(1);

    }

    /**
     * Adds the usage of the current entry by the given value
     *
     * Author : gandomi
     *
     * @param in_addedFrequence
     *
     */
    public void addFrequence(int in_addedFrequence) {
        frequence += in_addedFrequence;

    }

    public Integer getFrequence() {
        return frequence;
    }

    public ParseDefinition getParseDefinition() {
        return parseDefinition;
    }

    public void setParseDefinition(ParseDefinition parseDefinition) {
        this.parseDefinition = parseDefinition;
    }

    /**
     * This method updates the value maps. You need to have set the parse
     * definition for this method to work. If you want more specific
     * implementations of the map, like using different types other than String
     * we suggest that you create an extension of this class and override this
     * method.
     *
     * Author : gandomi
     *
     * @param in_valueMap
     *        A map of Strings pertaining to the values fetched from the log
     *
     */
    public void setValuesFromMap(Map<String, String> in_valueMap) {

        if (getParseDefinition() == null) {
            throw new IllegalStateException(
                    "You need to set the Parse Definition in order to fetch the values from the map.");
        }

        for (ParseDefinitionEntry lt_definition : getParseDefinition().getDefinitionEntries().stream()
                .filter(pd -> pd.isToPreserve()).collect(Collectors.toList())) {

            valuesMap.put(lt_definition.getTitle(), in_valueMap.get(lt_definition.getTitle()));
        }

        valuesMap.put(ParseDefinition.STD_DATA_FREQUENCE, this.getFrequence());
        valuesMap.put(ParseDefinition.STD_DATA_KEY, this.makeKey());

    }

}
