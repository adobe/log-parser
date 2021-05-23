/**
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract class for multiple definitions
 *
 *
 * Author : gandomi
 *
 */
public abstract class StdLogEntry {
    protected static Logger log = LogManager.getLogger();
    
    private Integer frequence = 1;
    private ParseDefinition parseDefinition;

    Map<String, Object> valuesMap = new HashMap<>();

    protected static final String STD_DATA_KEY = "key";

    protected static final String STD_DATA_FREQUENCE = "frequence";

    public abstract String makeKey();

    public StdLogEntry(ParseDefinition in_definition) {
        this.parseDefinition = in_definition;
    }

    public StdLogEntry(StdLogEntry in_oldLogEntry) {
        super();
        this.frequence = in_oldLogEntry.frequence;
        this.parseDefinition = in_oldLogEntry.parseDefinition;
        this.valuesMap = in_oldLogEntry.valuesMap;
    }

    public StdLogEntry() {
        parseDefinition = new ParseDefinition("Created By Default");
    }

    /**
     * Creates a clone of the current LogEntry. This requires that each child
     * defines a copy constructor
     *
     * Author : gandomi
     *
     * @return A new constructed LogEntry Object
     *
     */
    public abstract StdLogEntry copy();

    /**
     * Fetches a print out for listing purposed. It uses the header list as an
     * index to fetch the correct order of the values
     *
     * Author : gandomi
     *
     * @return a String for the print out
     *
     */
    public String fetchPrintOut() {
        List<String> l_printOutList = new ArrayList<>();

        final Map<String, Object> l_valueMap = this.fetchValueMap();

        l_printOutList.add(makeKey());
        for (String lt_header : this.fetchHeaders()) {

            l_printOutList.add(l_valueMap.get(lt_header).toString());
        }
        l_printOutList.add(getFrequence().toString());
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
    public abstract Set<String> fetchHeaders();

    /**
     * Returns a set of objects you have defined for your log class. When using
     * Genric Object no changes are made to it.
     *
     * Author : gandomi
     *
     * @return A Maps of extentions of StdLogEntry
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
     *        The amount we should add to the frequence
     *
     */
    public void addFrequence(int in_addedFrequence) {
        frequence += in_addedFrequence;

    }

    public Integer getFrequence() {
        return frequence;
    }

    /**
     * @param frequence
     *        the frequence to set
     */
    protected void setFrequence(Integer frequence) {
        this.frequence = frequence;
    }

    /**
     * @param valuesMap
     *        the valuesMap to set
     */
    protected void setValuesMap(Map<String, Object> valuesMap) {
        this.valuesMap = valuesMap;
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
                .filter(ParseDefinitionEntry::isToPreserve).collect(Collectors.toList())) {

            valuesMap.put(lt_definition.getTitle(), in_valueMap.get(lt_definition.getTitle()));
        }

    }

    public void put(String in_dataTitle, String in_value) {
        this.fetchValueMap().put(in_dataTitle, in_value);

    }

    public Object get(String in_dataTitle) {
        return this.fetchValueMap().get(in_dataTitle);
    }
    
    /**
     * Given a map of &lt;String,Object&gt; this method returns true if all of the map
     * values can be found in the values map of this StdLogEntry. If any of the
     * keys cannot be found in the valueMap, we provide a warning
     *
     * Author : gandomi
     *
     * @param in_filterMap
     *        A map of filter values
     * @return True if all of the values can be found in the valueMap
     *
     */
    public boolean matches(Map<String, Object> in_filterMap) {

        
        for (String lt_filterKey : in_filterMap.keySet()) {
            if (!this.fetchHeaders().contains(lt_filterKey)) {
                log.warn("The filter key {} could not be found among the log entry headers.", lt_filterKey);
                return false;
            }
            if (!this.get(lt_filterKey).equals(in_filterMap.get(lt_filterKey))) {
                return false;
            }
        }
        return true;
    }
    

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StdLogEntry other = (StdLogEntry) obj;
        if (frequence == null) {
            if (other.frequence != null)
                return false;
        } else if (!frequence.equals(other.frequence))
            return false;
        if (parseDefinition == null) {
            if (other.parseDefinition != null)
                return false;
        } else if (!parseDefinition.equals(other.parseDefinition))
            return false;
        if (valuesMap == null) {
            if (other.valuesMap != null)
                return false;
        } else if (!valuesMap.equals(other.valuesMap))
            return false;
        return true;
    }

}
