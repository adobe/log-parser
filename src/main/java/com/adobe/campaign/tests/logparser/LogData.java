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
}
