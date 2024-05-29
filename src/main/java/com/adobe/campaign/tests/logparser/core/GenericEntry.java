/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The generic entry is a standard string based definition where the values are
 * stored as is. I.e. strings. All definitions are based on the ParseDefinition
 * class
 * <p>
 * Author : gandomi
 */
public class GenericEntry extends StdLogEntry {

    /**
     * Constructor accepting a @{@link ParseDefinition object}
     * @param in_definition A @{@link ParseDefinition object}
     */
    public GenericEntry(ParseDefinition in_definition) {
        super(in_definition);
    }

    /**
     * Default constructor
     */
    public GenericEntry() {
        super(new ParseDefinition("Created By Default"));
    }

    private GenericEntry(GenericEntry l_inputData) {
        super(l_inputData);
    }

    @Override
    public String makeKey() {
        return String.join(getParseDefinition().getKeyPadding(), getParseDefinition().fetchKeyOrder().stream()
                .map(e -> valuesMap.get(e).toString()).collect(Collectors.toList()));
    }

    @Override
    public Set<String> fetchHeaders() {

        return getParseDefinition().fetchHeaders();

    }

    /**
     * Returns the values map as it is without any manipulation
     */
    @Override
    public Map<String, Object> fetchValueMap() {

        return getValuesMap();
    }

    @Override
    public GenericEntry copy() {
        return new GenericEntry(this);
    }

}
