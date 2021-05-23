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
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertThrows;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import com.adobe.campaign.tests.logparser.GenericEntry;
import com.adobe.campaign.tests.logparser.ParseDefinitionEntry;
import com.adobe.campaign.tests.logparser.StringParseFactory;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;

public class TestLogParsing {

    @Test
    public void testInstantiation() {
        ParseDefinitionEntry l_entry1 = new ParseDefinitionEntry("my title");

        assertThat("The constructor should set the title", l_entry1.getTitle(), is(equalTo("my title")));
    }

    @Test
    public void testSimpleLog() throws StringParseException {

        String logString = "2020-06-15T17:17:20.728Z    70011   70030   2   info    soap    Client request:#012<soapenv:Envelope#012#011xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"#012#011xmlns:urn=\"urn:xtk:session\">#012#011<soapenv:Header />#012#011<soapenv:Body>#012#011#011<urn:Logon>#012#011#011#011<urn:sessiontoken></urn:sessiontoken>#012#011#011#011<urn:strLogin>admin</urn:strLogin>#012#011#011#011<urn:strPassword>adminrd-dev54</urn:strPassword>#012#011#011#011<urn:elemParameters></urn:elemParameters>#012#011#011</urn:Logon>#012#011</soapenv:Body>#012</soapenv:Envelope>";

        ParseDefinitionEntry l_timeStamp = new ParseDefinitionEntry();
        l_timeStamp.setTitle("timeStamp");
        l_timeStamp.setStartStartOfLine();
        l_timeStamp.setEnd("    ");

        ParseDefinitionEntry l_verPart1 = new ParseDefinitionEntry();
        l_verPart1.setTitle("A");
        l_verPart1.setStart("Client request:");
        l_verPart1.setEnd("<soapenv");

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("data");
        l_verbDefinition2.setStart("xmlns:urn=\"urn:");
        l_verbDefinition2.setEnd("\"");
        l_verbDefinition2.setCaseSensitive(false);

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_timeStamp);
        l_definitionList.add(l_verPart1);
        l_definitionList.add(l_verbDefinition2);

        Map<String, String> l_searchMaps = StringParseFactory.parseString(logString, l_definitionList);

        assertThat(l_searchMaps.size(), is(equalTo(3)));
        assertThat("We should have entries", l_searchMaps.get("timeStamp"),
                is(equalTo("2020-06-15T17:17:20.728Z")));

    }

    @Test
    public void testParseDefinition() throws StringParseException {

        String logString = "2020-06-15T17:17:20.728Z    70011   70030   2   info    soap    Client request:#012<soapenv:Envelope#012#011xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"#012#011xmlns:urn=\"urn:xtk:session\">#012#011<soapenv:Header />#012#011<soapenv:Body>#012#011#011<urn:Logon>#012#011#011#011<urn:sessiontoken></urn:sessiontoken>#012#011#011#011<urn:strLogin>admin</urn:strLogin>#012#011#011#011<urn:strPassword>adminrd-dev54</urn:strPassword>#012#011#011#011<urn:elemParameters></urn:elemParameters>#012#011#011</urn:Logon>#012#011</soapenv:Body>#012</soapenv:Envelope>";

        ParseDefinition l_parseDefinition = new ParseDefinition("SOAP Log");

        assertThat("We should have the correct title", l_parseDefinition.getTitle(), is(equalTo("SOAP Log")));

        ParseDefinitionEntry l_timeStamp = new ParseDefinitionEntry();
        l_timeStamp.setTitle("timeStamp");
        l_timeStamp.setStartStartOfLine();
        l_timeStamp.setEnd("    ");

        l_parseDefinition.addEntry(l_timeStamp);

        ParseDefinitionEntry l_verPart1 = new ParseDefinitionEntry();
        l_verPart1.setTitle("A");
        l_verPart1.setStart("Client request:");
        l_verPart1.setEnd("<soapenv");

        l_parseDefinition.addEntry(l_verPart1);

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("data");
        l_verbDefinition2.setStart("xmlns:urn=\"urn:");
        l_verbDefinition2.setEnd("\"");
        l_verbDefinition2.setCaseSensitive(false);

        l_parseDefinition.addEntry(l_verbDefinition2);

        assertThat("We should have the same entries", l_parseDefinition.getDefinitionEntries().size(),
                is(equalTo(3)));

        Map<String, String> l_searchMaps = StringParseFactory.parseString(logString,
                l_parseDefinition.getDefinitionEntries());

        assertThat(l_searchMaps.size(), is(equalTo(3)));
        assertThat("We should have entries", l_searchMaps.get("timeStamp"),
                is(equalTo("2020-06-15T17:17:20.728Z")));

    }

    @Test
    public void testParseDefinition2() throws StringParseException {

        String logString = "2020-06-15T17:17:20.728Z    70011   70030   2   info    soap    Client request:#012<soapenv:Envelope#012#011xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"#012#011xmlns:urn=\"urn:xtk:session\">#012#011<soapenv:Header />#012#011<soapenv:Body>#012#011#011<urn:Logon>#012#011#011#011<urn:sessiontoken></urn:sessiontoken>#012#011#011#011<urn:strLogin>admin</urn:strLogin>#012#011#011#011<urn:strPassword>adminrd-dev54</urn:strPassword>#012#011#011#011<urn:elemParameters></urn:elemParameters>#012#011#011</urn:Logon>#012#011</soapenv:Body>#012</soapenv:Envelope>";

        ParseDefinition l_parseDefinition = new ParseDefinition("SOAP Log");

        assertThat("We should have the correct title", l_parseDefinition.getTitle(), is(equalTo("SOAP Log")));

        ParseDefinitionEntry l_timeStamp = new ParseDefinitionEntry();
        l_timeStamp.setTitle("timeStamp");
        l_timeStamp.setStartStartOfLine();
        l_timeStamp.setEnd("    ");

        l_parseDefinition.addEntry(l_timeStamp);

        ParseDefinitionEntry l_verPart1 = new ParseDefinitionEntry();
        l_verPart1.setTitle("A");
        l_verPart1.setStart("Client request:");
        l_verPart1.setEnd("<soapenv");

        l_parseDefinition.addEntry(l_verPart1);

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("data");
        l_verbDefinition2.setStart("xmlns:urn=\"urn:");
        l_verbDefinition2.setEnd("\"");
        l_verbDefinition2.setCaseSensitive(false);

        l_parseDefinition.addEntry(l_verbDefinition2);

        assertThat("We should have the same entries", l_parseDefinition.getDefinitionEntries().size(),
                is(equalTo(3)));

        Map<String, String> l_searchMaps = StringParseFactory.parseString(logString, l_parseDefinition);

        assertThat(l_searchMaps.size(), is(equalTo(3)));
        assertThat("We should have entries", l_searchMaps.get("timeStamp"),
                is(equalTo("2020-06-15T17:17:20.728Z")));

    }

    @Test
    public void testIssueWithTracking() throws StringParseException {
        String logString = "+41411:5ee88a26:0|POST /nl/jsp/soaprouter.jsp HTTP/1.1|Content-Type:application/soap+xml; action=nms%3aremoteTracking#GetTrackingLogs; charset=utf-8|Accept-Language:en|SOAPAction:nms%3aremoteTracking#GetTrackingLogs|Cookie:__sessiontoken=_-_fFZyJuDP9T1hV46cjTd1hFPCq7YBZ3YRg3WjSrhdyQ3TR/MPBgcy0lV15uSGq/apLM2b1RViEkJUIAIA|Host:rd-dev54.rd.campaign.adobe.com|Connection:keep-alive|Content-Length:720";

        //Create a parse definition

        ParseDefinition l_parseDefinition = new ParseDefinition("SOAP Log");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart("soapaction:");
        l_apiDefinition.setEnd("#");
        l_apiDefinition.setCaseSensitive(false);

        l_parseDefinition.addEntry(l_apiDefinition);

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd("|");

        l_parseDefinition.addEntry(l_verbDefinition);

        assertThat("The String should be compatible",
                StringParseFactory.isStringCompliant(logString, l_parseDefinition));

        assertThat("We should have found the correct path",
                StringParseFactory.fetchValue(logString, l_apiDefinition),
                is(equalTo("nms%3aremoteTracking")));

        Map<String, String> l_entries = StringParseFactory.parseString(logString, l_parseDefinition);

        assertThat("We should have the correct value for api", l_entries.get("path"),
                is(equalTo("nms%3aremoteTracking")));
    }

    @Test(description = "Checking that we correctly identify the date string")
    public void testFetchDateStringSTDMETHOD() throws ParseException, StringParseException {
        String l_resultString = "2330:DEBUG | 2020-04-03 17:46:38 | [main] core.NextTests (NextTests.java:209) - Before driver instantiation";

        //Create a parse definition
        ParseDefinitionEntry l_definition = new ParseDefinitionEntry();

        l_definition.setTitle("LogLevel");
        l_definition.setStart(" | ");
        l_definition.setEnd(" | ");

        assertThat(StringParseFactory.fetchValue(l_resultString, l_definition),
                is(equalTo("2020-04-03 17:46:38")));

    }

    @Test(description = "Checking that we correctly identify the line in the file source")
    public void testFetchFileLineSTDMETHOD() throws StringParseException {
        String l_resultString = "2330:DEBUG | 2020-04-03 17:46:38 | [main] core.NextTests (NextTests.java:209) - Before driver instantiation";

        //Create a parse definition
        ParseDefinitionEntry l_definition = new ParseDefinitionEntry();

        l_definition.setTitle("LogLevel");
        l_definition.setStart("a:");
        l_definition.setEnd(")");

        assertThat(StringParseFactory.fetchValue(l_resultString, l_definition), is(equalTo("209")));

    }

    @Test(description = "Checking that we correctly identify the file source")
    public void testFetchFileSTDMETHOD() throws StringParseException {
        String l_resultString = "2330:DEBUG | 2020-04-03 17:46:38 | [main] core.NextTests (NextTests.java:209) - Before driver instantiation";

        //Create a parse definition
        ParseDefinitionEntry l_definition = new ParseDefinitionEntry();

        l_definition.setTitle("LogLevel");
        l_definition.setStart("(");
        l_definition.setEnd(":");

        assertThat(StringParseFactory.fetchValue(l_resultString, l_definition),
                is(equalTo("NextTests.java")));

    }

    /********
     * Imported tests ; Harnesses
     * 
     * @throws StringParseException
     *********/
    @Test
    public void testFetchACCCoverageFromFile()
            throws InstantiationException, IllegalAccessException, StringParseException {

        //Create a parse definition

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_pDefinition = new ParseDefinition("ACC Coverage");
        l_pDefinition.setDefinitionEntries(Arrays.asList(l_apiDefinition, l_verbDefinition));

        final String apacheLogFile = "src/test/resources/logTests/acc/acc_integro_jenkins_log_exerpt.txt";

        Map<String, GenericEntry> l_entries = StringParseFactory
                .extractLogEntryMap(Arrays.asList(apacheLogFile), l_pDefinition, GenericEntry.class);

        assertThat(l_entries, is(notNullValue()));
        assertThat("We should have the correct nr of entries", l_entries.size(), is(equalTo(5)));
        assertThat("We should have the key for nms:delivery#PrepareFromId",
                l_entries.containsKey("nms:delivery#PrepareFromId"));

        assertThat(l_entries.get("xtk:persist#NewInstance").getFrequence(), is(equalTo(2)));

        for (GenericEntry lt_entry : l_entries.values()) {
            System.out.println(lt_entry.fetchPrintOut());
        }
    }

    @Test
    public void testItems() throws StringParseException {
        String l_accLogString = "INFO | 25-May-2020 03:29:28:097 | - (SOAPutils.java:434) - HEADER ACTION xtk:persist#NewInstance";

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_apiDefinition);
        l_definitionList.add(l_verbDefinition);

        Map<String, String> l_result = StringParseFactory.parseString(l_accLogString, l_definitionList);

        assertThat("We should have a result", l_result, is(notNullValue()));

        assertThat("We should have an entry for the api", l_result.containsKey("API"));
        assertThat("We should have the correct value for logDate", l_result.get("API"),
                is(equalTo("xtk:persist")));

        assertThat("We should have an entry for the verb", l_result.containsKey("verb"));
        assertThat("We should have the correct value for logDate", l_result.get("verb"),
                is(equalTo("NewInstance")));

    }

    @Test
    public void testUpdateMap() throws InstantiationException, IllegalAccessException, StringParseException {

        String l_accLogString = "INFO | 25-May-2020 03:29:28:097 | - (SOAPutils.java:434) - HEADER ACTION xtk:persist#NewInstance";

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(Arrays.asList(l_apiDefinition, l_verbDefinition));

        Map<String, GenericEntry> l_entryMap = new HashMap<String, GenericEntry>();

        StringParseFactory.updateEntryMapWithParsedData(l_accLogString, l_definition, l_entryMap,
                GenericEntry.class);

        assertThat("We should have one entry in the map", l_entryMap.size(), is(equalTo(1)));

        final GenericEntry l_entry = l_entryMap.values().iterator().next();

        assertThat("We should have the Definition stored in the generic entry", l_entry.getParseDefinition(),
                is(equalTo(l_definition)));
        assertThat("The key should be correct", l_entry.getFrequence(), is(equalTo(1)));
        assertThat("The key should be correct", l_entry.makeKey(), is(equalTo("xtk:persist#NewInstance")));

    }

    @Test
    public void testDefinitionCompare() {

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(Arrays.asList(l_apiDefinition, l_verbDefinition));

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition2 = new ParseDefinitionEntry();

        l_apiDefinition2.setTitle("API");
        l_apiDefinition2.setStart("HEADER ACTION ");
        l_apiDefinition2.setEnd("#");

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("#");
        l_verbDefinition2.setEnd(null);

        ParseDefinition l_definition2 = new ParseDefinition("Soap Call");
        l_definition2.setDefinitionEntries(Arrays.asList(l_apiDefinition2, l_verbDefinition2));

        assertThat("The two definitions are the same", l_definition, is(equalTo(l_definition2)));
        assertThat("The two hashes should be the same", l_definition.hashCode(),
                is(equalTo(l_definition2.hashCode())));

    }

    @Test
    public void testKey() {

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(Arrays.asList(l_apiDefinition, l_verbDefinition));
        l_definition.setKeyPadding("@");
        l_definition.defineKeys(Arrays.asList(l_verbDefinition, l_apiDefinition));

        GenericEntry l_entry = new GenericEntry(l_definition);

        Map<String, String> l_values = new HashMap<>();
        l_values.put("API", "A");
        l_values.put("verb", "team");

        l_entry.setValuesFromMap(l_values);

        assertThat("We should have the correct key", l_entry.makeKey(), is(equalTo("team@A")));
    }

    @Test(description = "In this case we give a non-existing definition item to the order")
    public void testKey_negative1() {

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(Arrays.asList(l_apiDefinition, l_verbDefinition));
        l_definition.setKeyPadding("@");

        //We define a definition entry which is not part of the definition
        ParseDefinitionEntry l_nonExistingDefinition = new ParseDefinitionEntry();
        l_verbDefinition.setTitle("Nada");

        assertThrows(IllegalArgumentException.class, () -> l_definition
                .defineKeys(Arrays.asList(l_verbDefinition, l_apiDefinition, l_nonExistingDefinition)));

    }

    @Test(description = "In this case we give a definition item to the order which is not kept during parsing")
    public void testKey_negative2() {

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_nonPreservedDefinition = new ParseDefinitionEntry();

        l_nonPreservedDefinition.setTitle("logos");
        l_nonPreservedDefinition.setStart("@@");
        l_nonPreservedDefinition.setEnd("é");
        l_nonPreservedDefinition.setToPreserve(false);

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(
                Arrays.asList(l_apiDefinition, l_verbDefinition, l_nonPreservedDefinition));
        l_definition.setKeyPadding("@");

        assertThrows(IllegalArgumentException.class, () -> l_definition
                .defineKeys(Arrays.asList(l_verbDefinition, l_apiDefinition, l_nonPreservedDefinition)));

    }

    @Test(description = "Testing when no specific order has been set")
    public void testKey_default() {

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_nonPreservedDefinition = new ParseDefinitionEntry();

        l_nonPreservedDefinition.setTitle("logos");
        l_nonPreservedDefinition.setStart("@@");
        l_nonPreservedDefinition.setEnd("é");
        l_nonPreservedDefinition.setToPreserve(false);

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(
                Arrays.asList(l_apiDefinition, l_verbDefinition, l_nonPreservedDefinition));

        assertThat(l_definition.fetchKeyOrder(),
                Matchers.contains(Arrays.asList(equalTo(l_apiDefinition), equalTo(l_verbDefinition))));

        GenericEntry l_entry = new GenericEntry(l_definition);

        Map<String, String> l_values = new HashMap<>();
        l_values.put("API", "A");
        l_values.put("verb", "team");

        l_entry.setValuesFromMap(l_values);

        assertThat("We should have the correct key", l_entry.makeKey(), is(equalTo("A#team")));
    }

    @Test(description = "Testing the case of just an entry value as key")
    public void testKeySimple() {

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(Arrays.asList(l_apiDefinition, l_verbDefinition));
        l_definition.setKeyPadding("@");
        l_definition.defineKeys(l_verbDefinition);

        GenericEntry l_entry = new GenericEntry(l_definition);

        l_entry.put("API", "A");
        l_entry.put("verb", "team");

        assertThat("We should have the correct key", l_entry.makeKey(), is(equalTo("team")));
    }

    @Test(description = "Checking that is to preserve is respected")
    public void testsetValuesFromMap_isToPreserveFalse() {

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_nonPreservedDefinition = new ParseDefinitionEntry();

        l_nonPreservedDefinition.setTitle("logos");
        l_nonPreservedDefinition.setStart("@@");
        l_nonPreservedDefinition.setEnd("é");
        l_nonPreservedDefinition.setToPreserve(false);

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(
                Arrays.asList(l_apiDefinition, l_verbDefinition, l_nonPreservedDefinition));

        assertThat(l_definition.fetchKeyOrder(),
                Matchers.contains(Arrays.asList(equalTo(l_apiDefinition), equalTo(l_verbDefinition))));

        GenericEntry l_entry = new GenericEntry(l_definition);

        Map<String, String> l_values = new HashMap<>();
        l_values.put("API", "A");
        l_values.put("verb", "team");
        l_values.put("logos", "prokorny");

        l_entry.setValuesFromMap(l_values);

        assertThat("We should have the correct key", l_entry.fetchValueMap().keySet(),
                Matchers.containsInAnyOrder("API", "verb"));
    }

    @Test(description = "We should be getting an error if the definition has not been defined")
    public void testsetValuesFromMap_NoDefinitionFalse() {

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_nonPreservedDefinition = new ParseDefinitionEntry();

        l_nonPreservedDefinition.setTitle("logos");
        l_nonPreservedDefinition.setStart("@@");
        l_nonPreservedDefinition.setEnd("é");
        l_nonPreservedDefinition.setToPreserve(false);

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(
                Arrays.asList(l_apiDefinition, l_verbDefinition, l_nonPreservedDefinition));

        GenericEntry l_entry = new GenericEntry();
        l_entry.setParseDefinition(null);

        Map<String, String> l_values = new HashMap<>();
        l_values.put("API", "A");
        l_values.put("verb", "team");
        l_values.put("logos", "prokorny");

        assertThrows(IllegalStateException.class, () -> l_entry.setValuesFromMap(l_values));
    }

    @Test
    public void testingPrintOut() {

        ParseDefinition l_pd = new ParseDefinition("ttt");
        ParseDefinitionEntry l_parseDefinitionEntry1 = new ParseDefinitionEntry();
        l_parseDefinitionEntry1.setTitle("verb");

        ParseDefinitionEntry l_parseDefinitionEntry2 = new ParseDefinitionEntry();
        l_parseDefinitionEntry2.setTitle("path");

        l_pd.addEntry(l_parseDefinitionEntry1);
        l_pd.addEntry(l_parseDefinitionEntry2);
        l_pd.setKeyPadding(" - ");
        final String testPrintOutPadding = "-";
        l_pd.setPrintOutPadding(testPrintOutPadding);

        GenericEntry l_ale = new GenericEntry(l_pd);

        Map<String, String> l_valueMap = new HashMap<>();
        l_valueMap.put("verb", "GET");
        l_valueMap.put("path", "abc");

        l_ale.setValuesFromMap(l_valueMap);

        assertThat("The keys should be the concatenation of verb and path", l_ale.makeKey(),
                is(equalTo("GET - abc")));

        assertThat(l_ale.fetchPrintOut(),
                is(equalTo(l_ale.makeKey() + testPrintOutPadding + l_ale.fetchValueMap().get("verb")
                        + testPrintOutPadding + l_ale.fetchValueMap().get("path") + testPrintOutPadding
                        + 1)));

    }

    @Test
    public void testFetchHeaders() {

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(Arrays.asList(l_apiDefinition, l_verbDefinition));

        assertThat(l_definition.fetchHeaders(), Matchers.contains("API", "verb"));

        GenericEntry l_entry = new GenericEntry(l_definition);

        assertThat("We should have the correct key", l_entry.fetchHeaders(),
                Matchers.contains("API", "verb"));
    }

    @Test
    public void testFetchHeaders_NotStored() {

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("API");
        l_apiDefinition.setStart("HEADER ACTION ");
        l_apiDefinition.setEnd("#");

        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("#");
        l_verbDefinition.setEnd(null);
        l_verbDefinition.setToPreserve(false);

        ParseDefinition l_definition = new ParseDefinition("Soap Call");
        l_definition.setDefinitionEntries(Arrays.asList(l_apiDefinition, l_verbDefinition));

        assertThat(l_definition.fetchHeaders(), Matchers.contains("API"));

        GenericEntry l_entry = new GenericEntry(l_definition);

        assertThat("We should have the correct key", l_entry.fetchHeaders(), Matchers.contains("API"));
    }
}
