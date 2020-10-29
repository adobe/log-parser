package com.adobe.campaign.tests.logparser;

import java.util.ArrayList;
import java.util.List;

public class ParseDefinition {

    private String title;
    private List<ParseDefinitionEntry> definitionEntries;

    public ParseDefinition(String in_title) {
        setTitle(in_title);
        definitionEntries = new ArrayList<>();
    }

    /**
     * This method adds a definition entry to the definition entries of this parse definition
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
