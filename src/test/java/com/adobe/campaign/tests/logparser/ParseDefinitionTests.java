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

import org.testng.annotations.Test;

public class ParseDefinitionTests {

    @Test
    public void testcopyConstructorParseDefinitionEntry() {

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("\"");
        l_verbDefinition.setEnd(" /");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart(" /rest/head/");
        l_apiDefinition.setEnd(" ");

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry(l_verbDefinition);

        assertThat("The two entries should be the same", l_verbDefinition, equalTo(l_verbDefinition2));

        ParseDefinitionEntry l_apDefinition2 = new ParseDefinitionEntry(l_apiDefinition);

        assertThat("The two new Definition entries should be different", l_apDefinition2,
                not(equalTo(l_verbDefinition2)));

    }
    

    @Test
    public void testcopyConstructorParseDefinition() {

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("\"");
        l_verbDefinition.setEnd(" /");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart(" /rest/head/");
        l_apiDefinition.setEnd(" ");

        ParseDefinition l_parseDefinition = new ParseDefinition("rest calls");
        l_parseDefinition.addEntry(l_apiDefinition);
        l_parseDefinition.addEntry(l_verbDefinition);
        
        ParseDefinition l_newParseDefinition = new ParseDefinition(l_parseDefinition);
        
        assertThat("The created parse definitions should be the same as the old one", l_parseDefinition, equalTo(l_newParseDefinition));
        
    }

    @Test
    public void testParseDefinitionEntryEquals1() {
        ParseDefinitionEntry l_pd1 = new ParseDefinitionEntry("A");
        ParseDefinitionEntry l_pd2 = new ParseDefinitionEntry("A");

        assertThat("The two classes should not be equal", l_pd1, not(equalTo(new GenericEntry())));

        l_pd1.setCaseSensitive(false);
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd1.setCaseSensitive(true);
        l_pd2.setEnd("D");
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd1.setEnd("B");
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd2.setEnd("B");
        l_pd2.setStart("D");
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd1.setStart("B");
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd2.setStart("B");

        l_pd1.setTitle(null);
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd2.setTitle(null);
        assertThat("The two classes should not be equal", l_pd1, equalTo(l_pd2));

        l_pd2.setTitle("D");
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd1.setTitle("B");
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd2.setTitle("B");

        l_pd2.setToPreserve(false);
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd2.setToPreserve(true);

        l_pd2.setTrimQuotes(true);
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        assertThat("The hashed should be different", l_pd1.hashCode(), not(equalTo(l_pd2.hashCode())));

        l_pd2.setTrimQuotes(false);
        assertThat("The two classes should now be equal", l_pd1, is(equalTo(l_pd2)));
        assertThat("The hashed should be the same", l_pd1.hashCode(), is(equalTo(l_pd2.hashCode())));

    }

    @Test
    public void testParseDefinitionEquals1() {
        ParseDefinition l_pd1 = new ParseDefinition("A");
        ParseDefinition l_pd2 = new ParseDefinition("A");

        assertThat("The two classes should not be equal", l_pd1, not(equalTo(new GenericEntry())));

        l_pd1.setDefinitionEntries(null);
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd2.setDefinitionEntries(null);
        assertThat("The two classes should  be equal", l_pd1, is(equalTo(l_pd2)));

    }

    @Test
    public void testParseDefinitionEquals2() {
        ParseDefinition l_pd1 = new ParseDefinition("A");
        ParseDefinition l_pd2 = new ParseDefinition("A");
        l_pd2.addEntry(new ParseDefinitionEntry("B"));

        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd1.addEntry(new ParseDefinitionEntry("B"));
        assertThat("The two classes should  be equal", l_pd1, is(equalTo(l_pd2)));

        l_pd1.setTitle(null);
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

        l_pd2.setTitle(null);
        assertThat("The two classes should  be equal", l_pd1, is(equalTo(l_pd2)));

        l_pd1.setTitle("A");
        l_pd2.setTitle("B");
        assertThat("The two classes should not be equal", l_pd1, not(equalTo(l_pd2)));

    }

}
