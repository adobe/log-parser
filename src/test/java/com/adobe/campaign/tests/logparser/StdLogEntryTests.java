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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.HashMap;

import org.testng.annotations.Test;

public class StdLogEntryTests {

    @Test
    public void testSimplePut()  {
        
        GenericEntry l_inputData = new GenericEntry();
        l_inputData.fetchValueMap().put("AAZ", "12");
        
        GenericEntry l_inputData2 = new GenericEntry();
        l_inputData2.put("AAZ", "12");

        assertThat(l_inputData, is(equalTo(l_inputData2)));
    }
    
    @Test
    public void testSimpleGet()  {
        
        
        GenericEntry l_inputData2 = new GenericEntry();
        l_inputData2.put("AAZ", "12");

        assertThat(l_inputData2.get("AAZ"), is(equalTo("12")));
    }
    
    @Test
    public void testEqualsSimple() {
        StdLogEntry l_myEntry = new GenericEntry();
        
        StdLogEntry l_myEntry2 = new GenericEntry();
        
        assertThat("Both should be equal", l_myEntry, is(equalTo(l_myEntry)));
        assertThat("Both should be equal", l_myEntry, is(equalTo(l_myEntry2)));
        
        assertThat("One being null is not allowed", l_myEntry, not(equalTo(null)));
        
        assertThat("One bein null is not allowed", l_myEntry, not(equalTo(new LogData<>())));    
        
        l_myEntry.setFrequence(null);
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));
        
        
        l_myEntry2.setFrequence(null);
        assertThat("Both should  be equal", l_myEntry, equalTo(l_myEntry2));
        l_myEntry.setFrequence(1);
        
        l_myEntry2.setFrequence(2);
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));
        l_myEntry2.setFrequence(1);
        
        l_myEntry.setParseDefinition(null);
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));
        
        l_myEntry2.setParseDefinition(null);
        assertThat("Both should  be equal", l_myEntry, equalTo(l_myEntry2));
        
        l_myEntry.setParseDefinition(new ParseDefinition("A"));
        l_myEntry2.setParseDefinition(new ParseDefinition("B"));
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));
        
        l_myEntry.setParseDefinition(new ParseDefinition("B"));
        
        
        l_myEntry.setValuesMap(null);
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));
        
        l_myEntry2.setValuesMap(null);
        assertThat("Both should  be equal", l_myEntry, equalTo(l_myEntry2));
        
        l_myEntry2.setValuesMap(new HashMap<String, Object>());
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));
        
        l_myEntry.setValuesMap(new HashMap<>());
        l_myEntry.put("A", "B");
        
        l_myEntry2.put("C", "D");
        
        assertThat("Both should not be equal", l_myEntry, not(equalTo(l_myEntry2)));
        
        
    }
}
