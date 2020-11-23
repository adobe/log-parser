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

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.adobe.campaign.tests.logparser.exceptions.IncorrectParseDefinitionTitleException;

public class LogData<T extends StdLogEntry> {

    protected static Logger log = LogManager.getLogger();
    /**
     * This value is both csv header, and a JSON selector
     */
    private Map<String, T> cubeData = new HashMap<>();

    public LogData(T in_stdLogEnDataData) {
        this.getCubeData().put(in_stdLogEnDataData.makeKey(), in_stdLogEnDataData);
    }

    public LogData(Map<String, T> in_logMap) {
        this.setCubeData(in_logMap);
    }

    public Map<String, T> getCubeData() {
        return cubeData;
    }

    public void setCubeData(Map<String, T> in_logMap) {
        this.cubeData = in_logMap;
    }

    /**
     * This method allows you to access a value within the cube map. For this
     * you need the key of the Data and the title of the value
     *
     * Author : gandomi
     *
     * @param in_dataEntryKey
     * @param in_valueKey
     * @return
     * @throws IncorrectParseDefinitionTitleException 
     *
     */
    public Object get(String in_dataEntryKey, String in_valueKey) throws IncorrectParseDefinitionTitleException {

        final T l_foundCubeEntry = this.getCubeData().get(in_dataEntryKey);

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
        if (cubeData == null) {
            if (other.cubeData != null)
                return false;
        } else if (!cubeData.equals(other.cubeData))
            return false;
        return true;
    }
}
