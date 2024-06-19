/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import com.adobe.campaign.tests.logparser.exceptions.IncorrectParseDefinitionException;
import com.adobe.campaign.tests.logparser.exceptions.LogDataExportToFileException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The main log object that contains the log information
 *
 * @param <T> The log information is always of the type @{@link StdLogEntry}
 */
public class LogData<T extends StdLogEntry> {

    protected static Logger log = LogManager.getLogger();

    /**
     * This value is both csv header, and a JSON selector
     */
    private Map<String, T> entries = new HashMap<>();

    /**
     * A standard LogData constructor
     *
     * @param in_stdLogEnDataData An object of the type @{@link StdLogEntry}
     */
    public LogData(T in_stdLogEnDataData) {
        this.addEntry(in_stdLogEnDataData);
    }

    /**
     * A map of String and @{@link StdLogEntry}
     *
     * @param in_logMap A Map of generated Keys and @{@link StdLogEntry data}
     */
    public LogData(Map<String, T> in_logMap) {
        this.setEntries(in_logMap);
    }

    /**
     * Default constructor
     */
    public LogData() {
    }

    public Map<String, T> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, T> in_logMap) {
        this.entries = in_logMap;
    }

    /**
     * This method adds an entry to the log data. If the entry already exists we just increment the frequence
     * <p>
     * Author : gandomi
     * <p>
     *
     * @param lt_cubeEntry An object of the type {@link StdLogEntry}
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
     * This method allows you to access an entry in the log data. For this you need the key of the Data
     * <p>
     * Author : gandomi
     *
     * @param in_dataEntryKey The key with which the data has been stored
     * @return The StdLogEntry for the given entry. null if not found
     */
    public T get(String in_dataEntryKey) {
        return this.getEntries().get(in_dataEntryKey);
    }

    /**
     * This method allows you to access a value within the cube map. For this you need the key of the Data and the title
     * of the value
     * <p>
     * Author : gandomi
     *
     * @param in_dataEntryKey The key with which the data has been stored
     * @param in_valueKey     The identity of the value.
     * @return The key value for the given entry. null if not found
     * @throws IncorrectParseDefinitionException If the given valueKey was not found in the definition
     */
    public Object get(String in_dataEntryKey, String in_valueKey) throws IncorrectParseDefinitionException {

        final T l_foundCubeEntry = this.get(in_dataEntryKey);

        if (l_foundCubeEntry != null) {
            if (!l_foundCubeEntry.fetchHeaders().contains(in_valueKey)) {
                throw new IncorrectParseDefinitionException("The key " + in_valueKey
                        + " was not defined in the parse definition for the ParseDefinition "
                        + l_foundCubeEntry.getParseDefinition().getTitle() + " you have configured.");
            }

            return l_foundCubeEntry.fetchValueMap().get(in_valueKey);
        }

        return null;
    }

    /**
     * This method allows you to change a specific value in the log data. For this, you need the key and the parse
     * definition title to find the value
     * <p>
     * Author : gandomi
     *
     * @param in_dataEntryKey The key with which the data has been stored
     * @param in_valueKey     The identity of the value.
     * @param in_newValue     The new value of the entry value
     * @throws IncorrectParseDefinitionException When there is no entry for the given in_dataEntryKey and in_valueKey
     */
    public void put(String in_dataEntryKey, String in_valueKey, Object in_newValue)
            throws IncorrectParseDefinitionException {
        if (get(in_dataEntryKey, in_valueKey) == null) {
            throw new IncorrectParseDefinitionException("The given parse definition entry for key "
                    + in_dataEntryKey + " and the ParsedefinitionEntry title " + in_valueKey
                    + " could not be found. Operation failed.");
        }

        final T l_foundCubeEntry = this.get(in_dataEntryKey);

        l_foundCubeEntry.fetchValueMap().put(in_valueKey, in_newValue);

    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LogData<?> other = (LogData<?>) obj;
        if (entries == null) {
            if (other.entries != null) {
                return false;
            }
        } else if (!entries.equals(other.entries)) {
            return false;
        }
        return true;
    }

    /**
     * Here we create a new LogDataObject with the given ParseDefinitionEntry. This method performs a groupby for the
     * given value. The frequence will also take into account the original frequence
     * <p>
     * Author : gandomi
     *
     * @param in_parseDefinitionEntryKey The key name of the parse definition perform the GroupBy on
     * @param in_transformationClass     The class to which we should transform the cube data
     * @param <U>                        The return type of the group by cube.
     * @return a new LogData Object containing the groupBy values
     * @throws IncorrectParseDefinitionException If the key is not in the ParseDefinitions of the Log data entry
     */
    public <U extends StdLogEntry> LogData<U> groupBy(String in_parseDefinitionEntryKey,
            Class<U> in_transformationClass)
            throws IncorrectParseDefinitionException {

        return groupBy(Arrays.asList(in_parseDefinitionEntryKey), in_transformationClass);
    }

    /**
     * Here we create a new LogDataObject with the given ParseDefinitionEntry. This method performs a groupby for the
     * given value. The frequence will also take into account the original frequence
     * <p>
     * Author : gandomi
     *
     * @param in_parseDefinitionEntryKeyList The list of key names of the parse definition perform the GroupBy on
     * @param in_transformationClass         The class to which we should transform the cube data
     * @param <U>                            The return type of the group by cube.
     * @return a new LogData Object containing the groupBy values
     * @throws IncorrectParseDefinitionException If the key is not in the ParseDefinitions of the Log data entry
     */
    public <U extends StdLogEntry> LogData<U> groupBy(List<String> in_parseDefinitionEntryKeyList,
            Class<U> in_transformationClass)
            throws IncorrectParseDefinitionException {
        LogData<U> lr_cubeData = new LogData<>();

        //Creating new Definition
        ParseDefinition l_cubeDefinition = new ParseDefinition(
                "cube " + String.join("-", in_parseDefinitionEntryKeyList));

        for (String lt_keyName : in_parseDefinitionEntryKeyList) {
            l_cubeDefinition.addEntry(new ParseDefinitionEntry(lt_keyName));
        }

        //Filling STDLogData
        for (T lt_entry : getEntries().values()) {
            Map<String, String> lt_cubeEntryValues = new HashMap<>();
            U lt_cubeEntry = null;
            try {
                lt_cubeEntry = in_transformationClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            lt_cubeEntry.setParseDefinition(l_cubeDefinition);

            for (String lt_parseDefinitionEntryKey : in_parseDefinitionEntryKeyList) {
                if (!lt_entry.getParseDefinition().fetchHeaders().contains(lt_parseDefinitionEntryKey)) {
                    throw new IncorrectParseDefinitionException("The given header name "
                            + lt_parseDefinitionEntryKey + " was not among the stored data");
                }

                lt_cubeEntryValues.put(lt_parseDefinitionEntryKey,
                        lt_entry.get(lt_parseDefinitionEntryKey).toString());

            }
            lt_cubeEntry.setValuesFromMap(lt_cubeEntryValues);
            lt_cubeEntry.setFrequence(lt_entry.getFrequence());

            lr_cubeData.addEntry(lt_cubeEntry);
        }
        return lr_cubeData;
    }

    /**
     * Here we create a new LogDataObject with the given ParseDefinitionEntry. This method performs a groupby for the
     * given value. The frequence will also take into account the original frequence
     * <p>
     * Author : gandomi
     *
     * @param in_parseDefinitionEntryKeyList The list of key names of the parse definition perform the GroupBy on
     * @return a new LogData Object containing the groupBy values
     * @throws IncorrectParseDefinitionException If the key is not in the ParseDefinitions of the Log data entry
     */
    public LogData<GenericEntry> groupBy(List<String> in_parseDefinitionEntryKeyList)
            throws IncorrectParseDefinitionException {
        return groupBy(in_parseDefinitionEntryKeyList, GenericEntry.class);
    }

    /**
     * Here we create a new LogDataObject with the given ParseDefinitionEntry. This method performs a groupby for the
     * given value. The frequence will also take into account the original frequence
     * <p>
     * Author : gandomi
     *
     * @param in_parseDefinitionEntryKey The key name of the parse definition perform the GroupBy on
     * @return a new LogData Object containing the groupBy values
     * @throws IncorrectParseDefinitionException If the key is not in the ParseDefinitions of the Log data entry
     */
    public LogData<GenericEntry> groupBy(String in_parseDefinitionEntryKey)
            throws IncorrectParseDefinitionException {
        return groupBy(in_parseDefinitionEntryKey, GenericEntry.class);
    }

    /**
     * This method filters the LogData with the given properties
     * <p>
     * Author : gandomi
     *
     * @param in_filterKeyValues A map of &lt;String,Object&gt; representation the values we want to find
     * @return a new LogDataObject containing only the filtered values
     */
    public LogData<T> filterBy(Map<String, Object> in_filterKeyValues) {
        LogData<T> lr_filteredLogData = new LogData<>();

        for (String lt_logDataKey : this.getEntries().keySet()) {
            if (this.get(lt_logDataKey).matches(in_filterKeyValues)) {
                lr_filteredLogData.addEntry(this.get(lt_logDataKey));
            }
        }

        return lr_filteredLogData;
    }

    /**
     * This method searches the LogData for an enry with a specific value for a parse definition entry name
     * <p>
     * Author : gandomi
     *
     * @param in_parseDefinitionName The name of the parse definition entry under which we search for a value
     * @param in_searchValue         The search value
     * @return a new LogDataObject containing only the searched values
     */
    public LogData<T> searchEntries(String in_parseDefinitionName, String in_searchValue) {
        Map<String, Object> l_filterProperties = new HashMap<>();
        l_filterProperties.put(in_parseDefinitionName, in_searchValue);

        return this.filterBy(l_filterProperties);
    }

    /**
     * This method searches the LogData with the given properties
     * <p>
     * Author : gandomi
     *
     * @param in_searchKeyValues A map of &lt;String,Object&gt; representation the values we want to find
     * @return a new LogDataObject containing only the filtered values
     */
    public LogData<T> searchEntries(Map<String, Object> in_searchKeyValues) {

        return filterBy(in_searchKeyValues);
    }

    /**
     * Lets us know if the given search term could be found.
     * <p>
     * Author : gandomi
     *
     * @param in_parseDefinitionName The name of the parse definition entry under which we search for a value
     * @param in_searchValue         The search value
     * @return true if the search terms could be found. Otherwise false
     */
    public boolean isEntryPresent(String in_parseDefinitionName, String in_searchValue) {
        Map<String, Object> l_searchProperties = new HashMap<>();
        l_searchProperties.put(in_parseDefinitionName, in_searchValue);

        return isEntryPresent(l_searchProperties);
    }

    /**
     * Lets us know if the given search terms could be found.
     * <p>
     * Author : gandomi
     *
     * @param in_searchKeyValues A map of &lt;String,Object&gt; representation the values we want to find
     * @return true if the search terms could be found. Otherwise false
     */
    public boolean isEntryPresent(Map<String, Object> in_searchKeyValues) {
        return searchEntries(in_searchKeyValues).getEntries().size() > 0;
    }

    /**
     * Exports the current LogData to a standard CSV file. By default the file will have an escaped version of the Parse
     * Definition as the name
     *
     * @return a CSV file containing the LogData
     * @throws LogDataExportToFileException If the file could not be exported
     */
    public File exportLogDataToCSV() throws LogDataExportToFileException {
        Optional<T> l_firstEntry = this.getEntries().values().stream().findFirst();

        if (l_firstEntry.isPresent()) {
            return exportLogDataToCSV(l_firstEntry.get().fetchHeaders(), l_firstEntry.get().getParseDefinition()
                    .fetchEscapedTitle()
                    + "-export.csv");
        } else {
            log.warn("No Log data to export. Please load the log data before re-attempting");
            return new File("Non-ExistingFile");
        }

    }

    /**
     * Exports the current LogData to a CSV file.
     *
     * @param in_headerSet   A set of headers to be used as keys for exporting
     * @param in_csvFileName The file name to export
     * @return a CSV file containing the LogData
     * @throws LogDataExportToFileException If the file could not be exported
     */
    public File exportLogDataToCSV(Set<String> in_headerSet, String in_csvFileName)
            throws LogDataExportToFileException {
        File l_exportFile = new File(in_csvFileName);

        if (l_exportFile.exists()) {
            log.info("Deleting existing log export file {}...", in_csvFileName);
            if (!l_exportFile.delete()) {
                throw new LogDataExportToFileException("We were unable to delete the file " + l_exportFile.getPath());
            }
        }

        try (CSVPrinter printer = new CSVPrinter(new FileWriter(in_csvFileName), CSVFormat.DEFAULT)) {
            printer.printRecord(in_headerSet);

            for (StdLogEntry lt_entry : this.getEntries().values()) {
                Map lt_values = lt_entry.fetchValueMap();
                printer.printRecord(in_headerSet.stream().map(h -> lt_values.get(h)).collect(Collectors.toList()));
            }

        } catch (IOException ex) {
            throw new LogDataExportToFileException("Encountered error while exporting the log data to a CSV file.", ex);
        }

        return new File(in_csvFileName);
    }

    public Map<String, LogDataComparison> compare(LogData<T> in_logData) {
        Map<String, LogDataComparison> lr_diff = new HashMap<>();


        for (String lt_key : this.getEntries().keySet()) {
            if (!in_logData.getEntries().containsKey(lt_key)) {
                lr_diff.put(lt_key,new LogDataComparison(this.get(lt_key), LogDataComparison.ChangeType.REMOVED,
                        this.get(lt_key).getFrequence(),0));
        } else if (in_logData.get(lt_key).getFrequence() != this.get(lt_key).getFrequence()) {

                lr_diff.put(lt_key, new LogDataComparison(in_logData.get(lt_key), LogDataComparison.ChangeType.MODIFIED,
                        this.get(lt_key).getFrequence(),in_logData.get(lt_key).getFrequence()));
            }
        }

        for (String lt_key : in_logData.getEntries().keySet()) {
            if (!this.getEntries().containsKey(lt_key)) {
                lr_diff.put(lt_key, new LogDataComparison(in_logData.get(lt_key), LogDataComparison.ChangeType.ADDED,
                        0, in_logData.get(lt_key).getFrequence()));
            }
        }

        return lr_diff;
    }
}
