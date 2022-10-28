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
package com.adobe.campaign.tests.logparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The main class for analyzing logs. It gathers a list of Pars Definiton
 * Entries and allows for defining high level definitions of how the gathered
 * data are to be analyzed.
 * 
 * In this class you can also define how each gathered entry should be unique
 * identified.
 *
 *
 * Author : gandomi
 *
 */
public class ParseDefinition {

    private String title;
    private List<ParseDefinitionEntry> definitionEntries;
    private String keyPadding = "#";
    private List<String> keyOrder;
    private String printOutPadding = ";";

    public ParseDefinition() {
        super();
        definitionEntries = new ArrayList<>();
        keyOrder = new ArrayList<>();
    }

    /**
     * The main constructor for the ParseDefiition
     * 
     * @param in_title
     *        The name of this parse definition group
     *
     *        Author : gandomi
     */
    public ParseDefinition(String in_title) {
        setTitle(in_title);
        definitionEntries = new ArrayList<>();
        keyOrder = new ArrayList<>();
    }

    public ParseDefinition(ParseDefinition in_oldParseDefinition) {
        super();
        this.title = in_oldParseDefinition.title;
        this.definitionEntries = in_oldParseDefinition.definitionEntries;
        this.keyPadding = in_oldParseDefinition.keyPadding;
        this.keyOrder = in_oldParseDefinition.keyOrder;
        this.printOutPadding = in_oldParseDefinition.printOutPadding;
    }

    /**
     * This method adds a definition entry to the definition entries of this
     * parse definition
     *
     * Author : gandomi
     *
     * @param in_parseDefinitionEntry
     *        a {@link ParseDefinitionEntry} which represents a step in the
     *        rules for analyzing a line.
     *
     */
    public void addEntry(ParseDefinitionEntry in_parseDefinitionEntry) {
        getDefinitionEntries().add(in_parseDefinitionEntry);

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDefinitionEntries() == null) ? 0 : getDefinitionEntries().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ParseDefinition))
            return false;
        ParseDefinition other = (ParseDefinition) obj;
        if (getDefinitionEntries() == null) {
            if (other.getDefinitionEntries() != null)
                return false;
        } else if (!getDefinitionEntries().equals(other.getDefinitionEntries()))
            return false;
        if (getTitle() == null) {
            if (other.getTitle() != null)
                return false;
        } else if (!getTitle().equals(other.getTitle()))
            return false;
        return true;
    }

    public List<ParseDefinitionEntry> getDefinitionEntries() {
        return definitionEntries;
    }

    /**
     * @param definitionEntries
     *        the definitionEntries to set
     */
    public void setDefinitionEntries(List<ParseDefinitionEntry> definitionEntries) {
        this.definitionEntries = definitionEntries;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyPadding() {
        return keyPadding;
    }

    public void setKeyPadding(String keyPadding) {
        this.keyPadding = keyPadding;
    }

    /**
     * If the keyOrder is not set if will by default return all the values that
     * are preserved
     *
     * Author : gandomi
     *
     * @return if no keyOrder was defined it will return a list of all of the
     *         preservable ParseDefinitionEntries
     *
     */
    public List<String> fetchKeyOrder() {
        if (keyOrder.isEmpty()) {
            return getDefinitionEntries().stream().filter(ParseDefinitionEntry::isToPreserve)
                    .map(t -> t.getTitle()).collect(Collectors.toList());
        }
        return keyOrder;
    }

    /**
     * Defines the keys and their order
     *
     * Author : gandomi
     *
     * @param in_keyOrderDefinitions
     *        A list of DefinitionEntries that define how the key should be
     *        built
     *
     */
    public void defineKeys(List<ParseDefinitionEntry> in_keyOrderDefinitions) {
        if (in_keyOrderDefinitions.stream().anyMatch(e -> !e.isToPreserve())) {
            throw new IllegalArgumentException(
                    "One of the key is flagged as 'not preserved' during log parsing, so this will not work.");
        }

        for (ParseDefinitionEntry lt_pdEntry : in_keyOrderDefinitions) {

            if (getDefinitionEntries().contains(lt_pdEntry)) {
                this.keyOrder.add(lt_pdEntry.getTitle());

            } else {
                throw new IllegalArgumentException("The definition entry with the title "
                        + lt_pdEntry.getTitle() + " was not defined for this parse definition.");

            }

        }

    }

    /**
     * Defines a definition entry that will be the key
     *
     * Author : gandomi
     *
     * @param in_parseDefinitionAsKey
     *        A {@link ParseDefinitionEntry} that functions as key
     *
     */
    public void defineKeys(ParseDefinitionEntry in_parseDefinitionAsKey) {
        defineKeys(Arrays.asList(in_parseDefinitionAsKey));

    }

    @JsonIgnore
    public String getPrintOutPadding() {
        return printOutPadding;
    }

    public void setPrintOutPadding(String printOutPadding) {
        this.printOutPadding = printOutPadding;
    }

    /**
     * Returns the headers (titles) of the stored definitions. We return each
     * stored value in the order they were defined. And at the end we add both
     * the key and the frequence
     *
     * Author : gandomi
     *
     * @return A sorted set of headers / keys used for storing the values
     *
     */
    public Set<String> fetchHeaders() {
        final Collection<String> l_definedHeaders = getDefinitionEntries().stream()
                .filter(ParseDefinitionEntry::isToPreserve).map(ParseDefinitionEntry::getTitle)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return (Set<String>) l_definedHeaders;
    }

    /**
     * @return the keyOrder
     */
    public List<String> getKeyOrder() {
        return keyOrder;
    }
}
