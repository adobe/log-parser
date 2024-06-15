/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import java.util.*;
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
    private String fileName;
    private String filePath;


    protected static final String STD_DATA_KEY = "key";

    protected static final String STD_DATA_FREQUENCE = "frequence";
    protected static final String STD_DATA_FILE_NAME = "fileName";
    protected static final String STD_DATA_FILE_PATH = "filePath";

    /**
     * A method that creates the key to identify each stored entry
     *
     * @return A constructed key
     */
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
     * Creates a clone of the current LogEntry. This requires that each child defines a copy constructor
     * <p>
     * Author : gandomi
     *
     * @return A new constructed LogEntry Object
     */
    public abstract StdLogEntry copy();

    /**
     * Returns the naw unchanged value map
     *
     * @return a map of generated keys and stored values
     */
    public Map<String, Object> getValuesMap() {
        return valuesMap;
    }

    /**
     * Fetches a print out for listing purposes. It uses the header list as an index to fetch the correct order of the
     * values
     * <p>
     * Author : gandomi
     *
     * @return a String for the print out
     */
    public String fetchPrintOut() {
        List<String> l_printOutList = fetchValuesAsList();
        return StringUtils.join(l_printOutList, this.getParseDefinition().getPrintOutPadding());
    }

    protected List<String> fetchValuesAsList() {
        List<String> l_printOutList = new ArrayList<>();

        final Map<String, Object> l_valueMap = this.fetchValueMap();

        l_printOutList.add(makeKey());
        if (getParseDefinition().isStoreFileName()) {
            l_printOutList.add(getFileName());
        }

        if (getParseDefinition().isStoreFilePath()) {
            l_printOutList.add(getFilePath());
        }
        for (String lt_header : this.fetchHeaders()) {

            l_printOutList.add(l_valueMap.get(lt_header).toString());
        }
        l_printOutList.add(getFrequence().toString());
        return l_printOutList;
    }

    /**
     * Fetches the headers that you have defined for the log class. You need to be careful that the headers you have
     * defined have been used in storing the values.
     * <p>
     * Author : gandomi
     *
     * @return a list of header names.
     */
    public abstract Set<String> fetchHeaders();

    /**
     * Returns a set of objects you have defined for your log class. When using Generic Object no changes are made to
     * it.
     * <p>
     * Author : gandomi
     *
     * @return A Maps of extentions of StdLogEntry
     */
    public abstract Map<String, Object> fetchValueMap();

    /**
     * Increments the frequence
     * <p>
     * Author : gandomi
     */
    protected void incrementUsage() {
        addFrequence(1);

    }

    /**
     * Adds the usage of the current entry by the given value
     * <p>
     * Author : gandomi
     *
     * @param in_addedFrequence The amount we should add to the frequence
     */
    public void addFrequence(int in_addedFrequence) {
        frequence += in_addedFrequence;

    }

    public Integer getFrequence() {
        return frequence;
    }

    /**
     * @param frequence the frequence to set
     */
    protected void setFrequence(Integer frequence) {
        this.frequence = frequence;
    }

    /**
     * @param valuesMap the valuesMap to set
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
     * This method updates the value maps. You need to have set the parse definition for this method to work. If you
     * want more specific implementations of the map, like using different types other than String we suggest that you
     * create an extension of this class and override this method.
     * <p>
     * Author : gandomi
     *
     * @param in_valueMap A map of Strings pertaining to the values fetched from the log
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
     * Given a map of &lt;String,Object&gt; this method returns true if all of the map values can be found in the values
     * map of this StdLogEntry. If any of the keys cannot be found in the valueMap, we provide a warning
     * <p>
     * Author : gandomi
     *
     * @param in_filterMap A map of filter values
     * @return True if all of the values can be found in the valueMap
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

    @Override
    public int hashCode() {
        return Objects.hash(frequence, parseDefinition, valuesMap);
    }

    /**
     * Returns the headers as they are stored
     * @return An ordered set of the value names that are stored
     */
    public Set<String> fetchStoredHeaders() {
        Set<String> lr_storedHeaders = new LinkedHashSet<>();
        lr_storedHeaders.add(STD_DATA_KEY);
        lr_storedHeaders.addAll(getParseDefinition().fetchHeaders());
        lr_storedHeaders.add(STD_DATA_FREQUENCE);
        return lr_storedHeaders;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setLogFileName(String in_logFile) {
        this.fileName = in_logFile;
    }

    public void setFilePath(String in_logFile) {
        this.filePath = in_logFile;
    }
}

