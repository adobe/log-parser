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
import java.util.Set;
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

    private static final String CSV_SEPARATOR = ";";
    private Integer frequence = 1;

    public abstract void setValuesFromMap(Map<String, String> in_valueMap);

    Map<String, Object> valuesMap = new HashMap<>();

    public abstract String makeKey();

    /**
     * Fetches a print out for listing purposed
     *
     * Author : gandomi
     *
     * @return
     *
     */
    public String fetchPrintOut() {
        List<String> l_printOutList = new ArrayList<>();
        
        final Map<String, Object> l_valueMap = this.fetchValueMap();
        
        for (String lt_header : this.fetchHeaders()) {

            l_printOutList.add(l_valueMap.get(lt_header).toString());
        }
        return StringUtils.join(l_printOutList, CSV_SEPARATOR);
    }

    public abstract Set<String> fetchHeaders();

    /**
     * Transforms the data in the Log entry into a List of Strings
     *
     * Author : gandomi
     *
     * @return
     *
     */
    public abstract Map<String, Object> fetchValueMap();

    public void incrementUsage() {
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

    /**
     * This method updates the value maps
     *
     * Author : gandomi
     *
     * @param in_valueMap
     * @param in_parseDefinitionList
     *
     */
    public void setValuesFromMap(Map<String, String> in_valueMap,
            List<ParseDefinitionEntry> in_parseDefinitionList) {
        setValuesFromMap(in_valueMap);
        for (ParseDefinitionEntry lt_definition : in_parseDefinitionList.stream().filter(pd -> pd.isToPreserve())
                .collect(Collectors.toList())) {

            valuesMap.put(lt_definition.getTitle(), in_valueMap.get(lt_definition.getTitle()));
        }
        

    }

}
