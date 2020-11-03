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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParseDefinition {

    private String title;
    private List<ParseDefinitionEntry> definitionEntries;
    private String keyPadding = "#";
    private List<ParseDefinitionEntry> keyOrder;
    private String printOutPadding = ";";

    protected static final String STD_DATA_FREQUENCE = "frequence";
    protected static final String STD_DATA_KEY = "key";

    public ParseDefinition(String in_title) {
        setTitle(in_title);
        definitionEntries = new ArrayList<>();
        keyOrder = new ArrayList<>();
    }

    /**
     * This method adds a definition entry to the definition entries of this
     * parse definition
     *
     * Author : gandomi
     *
     * @param in_parseDefinitionEntry
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
     * @return
     *
     */
    public List<ParseDefinitionEntry> fetchKeyOrder() {
        if (keyOrder.isEmpty()) {
            return getDefinitionEntries().stream().filter(ParseDefinitionEntry::isToPreserve).collect(Collectors.toList());
        }
        return keyOrder;
    }

    /**
     * Defines the order of the keys
     *
     * Author : gandomi
     *
     * @param in_keyOrderDefinitions
     *
     */
    public void defineKeyOrder(List<ParseDefinitionEntry> in_keyOrderDefinitions) {
        if (in_keyOrderDefinitions.stream().anyMatch(e -> !e.isToPreserve())) {
            throw new IllegalArgumentException(
                    "One of the key is flagged as 'not preserved' during log parsing, so this will not work.");
        }

        for (ParseDefinitionEntry lt_pdEntry : in_keyOrderDefinitions) {

            if (getDefinitionEntries().contains(lt_pdEntry)) {
                this.keyOrder.add(lt_pdEntry);

            } else {
                throw new IllegalArgumentException("The definition entry with the title "
                        + lt_pdEntry.getTitle() + " was not defined for this parse definition.");

            }

        }

    }

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
     * @return
     *
     */
    public Set<String> fetchHeaders() {
        final Collection<String> l_definedHeaders = getDefinitionEntries().stream().filter(ParseDefinitionEntry::isToPreserve)
                .map(ParseDefinitionEntry::getTitle).collect(Collectors.toCollection(LinkedHashSet::new));
        l_definedHeaders.add(STD_DATA_KEY);
        l_definedHeaders.add(STD_DATA_FREQUENCE);
        return (Set<String>) l_definedHeaders;
    }

}
