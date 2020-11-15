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

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The generic entry is a standard string based definition where the values are
 * stored as is. I.e. strings. All definitions are based on the ParseDefinition
 * class
 *
 *
 * Author : gandomi
 *
 */
public class GenericEntry extends StdLogEntry {

    public GenericEntry(ParseDefinition in_definition) {
        super(in_definition);
    }

    public GenericEntry() {
        super(new ParseDefinition("Created By Default"));
    }

    @Override
    public String makeKey() {
        return String.join(getParseDefinition().getKeyPadding(), getParseDefinition().fetchKeyOrder().stream()
                .map(e -> valuesMap.get(e.getTitle()).toString()).collect(Collectors.toList()));
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

        return valuesMap;
    }
    
}
