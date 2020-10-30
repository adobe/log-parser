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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        // TODO set to generic
        Set<String> lr_headerSet = new LinkedHashSet<>();
        lr_headerSet.addAll(Arrays.asList("key", "path", "verb", "frequence"));
        return lr_headerSet;

    }

    @Override
    public Map<String, Object> fetchValueMap() {
        
        return valuesMap;
    }


}
