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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.adobe.campaign.tests.logparser.exceptions.IncorrectParseDefinitionTitleException;

public class LogData<T extends StdLogEntry> {

    protected static Logger log = LogManager.getLogger();
    /**
     * This value is both csv header, and a JSON selector
     */
    private Map<String, T> entries = new HashMap<>();

    public LogData(T in_stdLogEnDataData) {
        this.addEntry(in_stdLogEnDataData);
    }

    public LogData(Map<String, T> in_logMap) {
        this.setEntries(in_logMap);
    }

    public LogData() {
    }

    public Map<String, T> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, T> in_logMap) {
        this.entries = in_logMap;
    }

    /**
     * This method adds an entry to the log data. If the entry already exists we
     * just increment the frequence
     *
     * Author : gandomi
     *
     * @param lt_cubeEntry
     *        An object of the type {@link StdLogEntry}
     *
     */
    public void addEntry(T lt_cubeEntry) {

        final String l_candidateKey = lt_cubeEntry.makeKey();

        if (entries.containsKey(l_candidateKey)) {
            entries.get(l_candidateKey).addFrequence(lt_cubeEntry.getFrequence());

        } else {
            entries.put(l_candidateKey, lt_cubeEntry);
        }
    }

    /**
     * This method allows you to access an entry in the log data. For this you
     * need the key of the Data
     *
     * Author : gandomi
     *
     * @param in_dataEntryKey
     *        The key with which the data has been stored
     * @return The StdLogEntry for the given entry. null if not found
     *
     */
    public T get(String in_dataEntryKey) {
        return this.getEntries().get(in_dataEntryKey);
    }

    /**
     * This method allows you to access a value within the cube map. For this
     * you need the key of the Data and the title of the value
     *
     * Author : gandomi
     *
     * @param in_dataEntryKey
     *        The key with which the data has been stored
     * @param in_valueKey
     *        The identity of the value.
     * @return The key value for the given entry. null if not found
     * @throws IncorrectParseDefinitionTitleException
     *         If the given valueKey was not found in the definition
     *
     */
    public Object get(String in_dataEntryKey, String in_valueKey)
            throws IncorrectParseDefinitionTitleException {

        final T l_foundCubeEntry = this.get(in_dataEntryKey);

        if (l_foundCubeEntry != null) {
            if (!l_foundCubeEntry.fetchHeaders().contains(in_valueKey)) {
                throw new IncorrectParseDefinitionTitleException("The key " + in_valueKey
                        + " was not defined in the parse definition for the ParseDefinition "
                        + l_foundCubeEntry.getParseDefinition().getTitle() + " you have configured.");
            }

            return l_foundCubeEntry.fetchValueMap().get(in_valueKey);
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LogData<?> other = (LogData<?>) obj;
        if (entries == null) {
            if (other.entries != null)
                return false;
        } else if (!entries.equals(other.entries))
            return false;
        return true;
    }

    /**
     * Here we create a new LogDataObject with the given ParseDefinitionEntry.
     * This method performs a groupby for the given value. The frequence will
     * also take into account the original frequence
     *
     * Author : gandomi
     *
     * @param in_parseDefinitionEntryKey
     *        The key name of the parse definition perform the GroupBy on
     * @param in_transformationClass
     *        The class to which we should transform the cube data
     * @param <U> The return type of the group by cube. 
     * @return a new LogData Object containing the groupBy values
     * @throws IncorrectParseDefinitionTitleException
     *         If the key is not in the ParseDefinitions of the Log data entry
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
     * @throws InstantiationException if this {@code Class} represents an abstract class, an interface,
     *         an array class, a primitive type, or void; or if the class has no
     *         nullary constructor; or if the instantiation fails for some other
     *         reason.
     *
     */
    public <U extends StdLogEntry> LogData<U> groupBy(String in_parseDefinitionEntryKey,
            Class<U> in_transformationClass)
            throws IncorrectParseDefinitionTitleException, InstantiationException, IllegalAccessException {

        return groupBy(Arrays.asList(in_parseDefinitionEntryKey), in_transformationClass);
    }

    /**
     * Here we create a new LogDataObject with the given ParseDefinitionEntry.
     * This method performs a groupby for the given value. The frequence will
     * also take into account the original frequence
     *
     * Author : gandomi
     *
     * @param in_parseDefinitionEntryKeyList
     *        The list of key names of the parse definition perform the GroupBy
     *        on
     * @param in_transformationClass
     *        The class to which we should transform the cube data
     * @param <U> The return type of the group by cube. 
     * @return a new LogData Object containing the groupBy values
     * @throws IncorrectParseDefinitionTitleException
     *         If the key is not in the ParseDefinitions of the Log data entry
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
     * @throws InstantiationException if this {@code Class} represents an abstract class, an interface,
     *         an array class, a primitive type, or void; or if the class has no
     *         nullary constructor; or if the instantiation fails for some other
     *         reason.
     *
     */
    public <U extends StdLogEntry> LogData<U> groupBy(List<String> in_parseDefinitionEntryKeyList,
            Class<U> in_transformationClass)
            throws IncorrectParseDefinitionTitleException, InstantiationException, IllegalAccessException {
        LogData<U> lr_cubeData = new LogData<U>();

        //Creating new Definition
        ParseDefinition l_cubeDefinition = new ParseDefinition(
                "cube " + String.join("-", in_parseDefinitionEntryKeyList));

        for (String lt_keyName : in_parseDefinitionEntryKeyList) {
            l_cubeDefinition.addEntry(new ParseDefinitionEntry(lt_keyName));
        }

        //Filling STDLogData
        for (T lt_entry : getEntries().values()) {
            Map<String, String> lt_cubeEntryValues = new HashMap<>();
            U lt_cubeEntry = in_transformationClass.newInstance();
            lt_cubeEntry.setParseDefinition(l_cubeDefinition);

            for (String lt_parseDefinitionEntryKey : in_parseDefinitionEntryKeyList) {
                if (!lt_entry.getParseDefinition().fetchHeaders().contains(lt_parseDefinitionEntryKey)) {
                    throw new IncorrectParseDefinitionTitleException("The given header name "
                            + lt_parseDefinitionEntryKey + " was not among the stored data");
                }

                lt_cubeEntryValues.put(lt_parseDefinitionEntryKey,
                        lt_entry.get(lt_parseDefinitionEntryKey).toString());

            }
            lt_cubeEntry.setValuesFromMap(lt_cubeEntryValues);

            lr_cubeData.addEntry(lt_cubeEntry);
        }
        return lr_cubeData;
    }

}
