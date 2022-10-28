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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;

import com.adobe.campaign.tests.logparser.GenericEntry;
import com.adobe.campaign.tests.logparser.ParseDefinitionEntry;
import com.adobe.campaign.tests.logparser.StringParseFactory;
import com.adobe.campaign.tests.logparser.exceptions.StringParseException;

public class ParseTesting {
    // private final String STD_GREP_STRING = IntegroGrepFactory.ACS_RestPath.regularExpression;

    @Test
    public void testItems() throws StringParseException {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_dateDefinition = new ParseDefinitionEntry();

        l_dateDefinition.setTitle("logDate");
        l_dateDefinition.setStart("[");
        l_dateDefinition.setEnd("]");

        assertThat("We should have the correct definitions for the name", l_dateDefinition.getTitle(),
                is(equalTo("logDate")));
        assertThat("We should have the correct definitions for the start string", l_dateDefinition.getStart(),
                is(equalTo("[")));
        assertThat("We should have the correct definitions for the end string", l_dateDefinition.getEnd(),
                is(equalTo("]")));

        Map<String, String> l_result = StringParseFactory.parseString(l_apacheLogString, l_dateDefinition);

        assertThat("We should have a result", l_result, is(notNullValue()));

        assertThat("We should have an entry for logDate", l_result.containsKey("logDate"));
        assertThat("We should have the correct value for logDate", l_result.get("logDate"),
                is(equalTo("02/Apr/2020:08:08:28 +0200")));

    }

    @Test
    public void testItemParseDefinitionOfASimpleDate() throws StringParseException {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_dateDefinition = new ParseDefinitionEntry();

        l_dateDefinition.setTitle("logDate");
        l_dateDefinition.setStart("[");
        l_dateDefinition.setEnd("]");

        assertThat("We should have the correct value for logDate",
                StringParseFactory.fetchValue(l_apacheLogString, l_dateDefinition),
                is(equalTo("02/Apr/2020:08:08:28 +0200")));

    }

    @Test(description = "A case where the separators are the same and larger than 1 character")
    public void testItemParseDefinitionOfASimpleDateCase2() throws StringParseException {
        String l_apacheLogString = "2330:DEBUG | 2020-04-03 17:46:38 | [main] core.NextTests (NextTests.java:209) - Before driver instantiation";

        //Create a parse definition
        ParseDefinitionEntry l_dateDefinition = new ParseDefinitionEntry();

        l_dateDefinition.setTitle("logDate");
        l_dateDefinition.setStart(" | ");
        l_dateDefinition.setEnd(" | ");

        assertThat("We should have the correct value for logDate",
                StringParseFactory.fetchValue(l_apacheLogString, l_dateDefinition),
                is(equalTo("2020-04-03 17:46:38")));

    }

    @Test
    public void testVerbDefinition() throws StringParseException {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        assertThat("We should have the correct value for verb",
                StringParseFactory.fetchValue(l_apacheLogString, l_verbDefinition2), is(equalTo("GET")));

    }

    @Test
    public void testAPIDefinition() throws StringParseException {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("api");
        l_verbDefinition2.setStart(" /rest/head/");
        l_verbDefinition2.setEnd(" ");

        assertThat("We should have the correct value for api",
                StringParseFactory.fetchValue(l_apacheLogString, l_verbDefinition2),
                is(equalTo("workflow/WKF193")));

    }

    @Test
    public void testCaseSensitive() throws StringParseException {
        String logString = "J_BfmC8mfw==|soapaction:xtk%3aqueryDef#ExecuteQuery|Content-Length:591";

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart("soapAction:");
        l_apiDefinition.setEnd("#");
        l_apiDefinition.setCaseSensitive(false);

        assertThat(l_apiDefinition.isCaseSensitive(), is(equalTo(false)));

        assertThat("We should have found the correct path",
                StringParseFactory.fetchValue(logString, l_apiDefinition), is(equalTo("xtk%3aqueryDef")));

        Map<String, String> l_entries = StringParseFactory.parseString(logString, l_apiDefinition);

        assertThat("We should have the correct value for api", l_entries.get("path"),
                is(equalTo("xtk%3aqueryDef")));

    }

    @Test
    public void testCaseSensitive2() throws StringParseException {
        String logString = "J_BfmC8mfw==|soapAction:xtk%3aqueryDef#ExecuteQuery|Content-Length:591";

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart("SOAPACTION:");
        l_apiDefinition.setEnd("#");
        l_apiDefinition.setCaseSensitive(false);

        assertThat(l_apiDefinition.isCaseSensitive(), is(equalTo(false)));

        assertThat("We should have found the correct path",
                StringParseFactory.fetchValue(logString, l_apiDefinition), is(equalTo("xtk%3aqueryDef")));

        Map<String, String> l_entries = StringParseFactory.parseString(logString, l_apiDefinition);

        assertThat("We should have the correct value for api", l_entries.get("path"),
                is(equalTo("xtk%3aqueryDef")));

    }

    @Test(description = "In this case we check if the class ApacheLogEntry correctly stores a path with filters")
    public void testAPIDefinition2() throws StringParseException {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:06:25:44 +0200] \"POST /rest/head/session HTTP/1.1\" 201 5385 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart(" /rest/head/");
        l_apiDefinition.setEnd(" ");

        ParseDefinition l_parseDefinition = new ParseDefinition("rest calls");
        l_parseDefinition.setDefinitionEntries(Arrays.asList(l_verbDefinition2, l_apiDefinition));

        Map<String, String> l_currentValues = StringParseFactory.parseString(l_apacheLogString,
                l_parseDefinition);

        assertThat("We should have two entries", l_currentValues.size(), is(equalTo(2)));

        assertThat("We should be able to find our path", l_currentValues.get("path"), is(equalTo("session")));

        GenericEntry l_entry = new GenericEntry(l_parseDefinition);
        l_entry.setValuesFromMap(l_currentValues);

        assertThat("We should have the correct value for api", l_entry.fetchValueMap().get("path"),
                is(equalTo("session")));

    }

    @Test
    public void testAPIDefinitionRestHead() throws StringParseException {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("api");
        l_verbDefinition2.setStart(" /rest/head/");
        l_verbDefinition2.setEnd("/");

        assertThat("We should have the correct value for api",
                StringParseFactory.fetchValue(l_apacheLogString, l_verbDefinition2), is(equalTo("workflow")));

    }

    @Test
    public void testAPIDefinitionRestHeadNegative() {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /xtk/logon.jssp HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("api");
        l_verbDefinition2.setStart(" /rest/head/");
        l_verbDefinition2.setEnd("/");

        assertThrows(StringParseException.class,
                () -> StringParseFactory.fetchValue(l_apacheLogString, l_verbDefinition2));

    }

    @Test
    public void testAPIDefinitionRestHeadNegative2() {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /xtk/logon.jssp HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("api");
        l_verbDefinition2.setStart(" /xtk/");
        l_verbDefinition2.setEnd("@");

        assertThrows(StringParseException.class,
                () -> StringParseFactory.fetchValue(l_apacheLogString, l_verbDefinition2));

    }

    @Test
    public void testAnswer() throws StringParseException {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("response");
        l_verbDefinition2.setStart("\" ");
        l_verbDefinition2.setEnd(" ");

        assertThat("We should have the correct value for api",
                StringParseFactory.fetchValue(l_apacheLogString, l_verbDefinition2), is(equalTo("200")));
    }

    @Test(description = "Checking that we correctly identify the message")
    public void testingStartOfLine() throws StringParseException {
        String logString = "2020-06-15T17:17:20.728Z    70011   70030   2   info    soap";

        ParseDefinitionEntry l_definition = new ParseDefinitionEntry();

        l_definition.setTitle("Time");
        l_definition.setStart(null);
        l_definition.setEnd("    ");

        assertThat("The end should be identitified as SOL", l_definition.isStartStartOfLine());

        assertThat("The end position should be the length of the string",
                l_definition.fetchEndPosition(logString), is(equalTo(24)));

        assertThat(StringParseFactory.fetchValue(logString, l_definition),
                is(equalTo("2020-06-15T17:17:20.728Z")));

        ParseDefinitionEntry l_definition2 = new ParseDefinitionEntry();

        l_definition2.setTitle("Time");
        l_definition2.setStartStartOfLine();
        l_definition2.setEnd("    ");

        assertThat("The end should be identitified as SOL", l_definition2.isStartStartOfLine());

    }

    @Test(description = "Checking that we correctly identify the message")
    public void testingStartOfLine2() throws StringParseException {
        String logString = "2020-06-15T17:17:20.728Z    70011   70030   2   info    soap";

        ParseDefinitionEntry l_definition = new ParseDefinitionEntry();

        l_definition.setTitle("Time");
        l_definition.setEnd("    ");

        assertThat("The end should be identitified as SOL", l_definition.isStartStartOfLine());

        assertThat("The end position should be the length of the string",
                l_definition.fetchEndPosition(logString), is(equalTo(24)));

        assertThat(StringParseFactory.fetchValue(logString, l_definition),
                is(equalTo("2020-06-15T17:17:20.728Z")));

    }

    @Test(description = "Checking that we correctly identify the message")
    public void testingEndOfLine() throws StringParseException {
        String l_resultString = " - Before driver instantiation";

        ParseDefinitionEntry l_definition = new ParseDefinitionEntry();

        l_definition.setTitle("sourceFileLine");
        l_definition.setStart(" - ");
        l_definition.setEndEOL();

        assertThat("The end should be identitified as EOL", l_definition.isEndEOL());

        assertThat("The end position should be the length of the string",
                l_definition.fetchEndPosition(l_resultString), is(equalTo(l_resultString.length())));

        assertThat(StringParseFactory.fetchValue(l_resultString, l_definition),
                is(equalTo("Before driver instantiation")));

    }

    @Test
    public void testStringCompliance() {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart(" /rest/head/");
        l_apiDefinition.setEnd("/");

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_verbDefinition2);
        l_definitionList.add(l_apiDefinition);

        assertThat("The given string does not comply to the expected format",
                StringParseFactory.isStringCompliant(l_apacheLogString, l_definitionList));
    }

    @Test
    public void testStringComplianceNegative1() {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.61 - - [02/Apr/2020:06:26:09 +0200] \"GET /xtk/logon.jssp?code=..--7--D4C89vHhLLCL_yJguuL3kiR3PRM0T-UaOCk_Q162y6HIbdL2YA9L1ErbA2NLnen-- HTTP/1.1\" 302 938 \"https://auth-stg1.services.adobe.com/en_US/index.html?callback=https%3A%2F%2Fims-na1-stg1.adobelogin.com%2Fims%2Fadobeid%2FCampaignRD1%2FAdobeID%2Fcode%3Fredirect_uri%3Dhttps%253A%252F%252Fafthost32.qa.campaign.adobe.com%252Fxtk%252Flogon.jssp&client_id=CampaignRD1&scope=AdobeID%2Cperson%2Csession%2Cadditional_info.projectedProductContext%2Cread_organizations%2Cadditional_info.user_image_url%2Cwrite_pc%2Copenid%2Ctriggers%2Caudiencemanager_api&denied_callback=https%3A%2F%2Fims-na1-stg1.adobelogin.com%2Fims%2Fdenied%2FCampaignRD1%3Fredirect_uri%3Dhttps%253A%252F%252Fafthost32.qa.campaign.adobe.com%252Fxtk%252Flogon.jssp%26response_type%3Dcode&relay=f1cff3df-18ac-44a2-bc5f-b89331c11084&locale=en_US&flow_type=code&idp_flow_type=login\" \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36\"";

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart(" /rest/head/");
        l_apiDefinition.setEnd("/");

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_verbDefinition2);
        l_definitionList.add(l_apiDefinition);

        assertThat("The given string does not comply to the expected format",
                not(StringParseFactory.isStringCompliant(l_apacheLogString, l_definitionList)));
    }

    @Test
    public void testStringComplianceNegative_searchStringDifferentCase() {
        String l_apacheLogString = "-:443 10.10.247.65 - - [02/Apr/2020:07:11:04 +0200] \"-\" 408 4640 \"-\" \"-\"";

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart(" /rest/head/");
        l_apiDefinition.setEnd("/");

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_verbDefinition2);
        l_definitionList.add(l_apiDefinition);

        assertThat("The given string does not comply to the expected format",
                not(StringParseFactory.isStringCompliant(l_apacheLogString, l_definitionList)));
    }

    @Test
    public void testTwoItems() throws StringParseException {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_dateDefinition1 = new ParseDefinitionEntry();

        l_dateDefinition1.setTitle("logDate");
        l_dateDefinition1.setStart("[");
        l_dateDefinition1.setEnd("]");

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_dateDefinition1);
        l_definitionList.add(l_verbDefinition2);

        Map<String, String> l_result = StringParseFactory.parseString(l_apacheLogString, l_definitionList);

        assertThat("We should have a result", l_result, is(notNullValue()));

        assertThat("We should have two entries", l_result.size(), is(equalTo(2)));

        assertThat("We should have an entry for logDate", l_result.containsKey("logDate"));
        assertThat("We should have the correct value for logDate", l_result.get("logDate"),
                is(equalTo("02/Apr/2020:08:08:28 +0200")));

        assertThat("We should have an entry for verb", l_result.containsKey("verb"));
        assertThat("We should have the correct value for logDate", l_result.get("verb"), is(equalTo("GET")));

    }

    @Test
    public void testThreeItems() throws StringParseException {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_dateDefinition1 = new ParseDefinitionEntry();

        l_dateDefinition1.setTitle("logDate");
        l_dateDefinition1.setStart("[");
        l_dateDefinition1.setEnd("]");

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart(" /");
        l_apiDefinition.setEnd(" ");

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_dateDefinition1);
        l_definitionList.add(l_verbDefinition2);
        l_definitionList.add(l_apiDefinition);

        Map<String, String> l_result = StringParseFactory.parseString(l_apacheLogString, l_definitionList);

        assertThat("We should have a result", l_result, is(notNullValue()));

        assertThat("We should have two entries", l_result.size(), is(equalTo(3)));

        assertThat("We should have an entry for logDate", l_result.containsKey("logDate"));
        assertThat("We should have the correct value for logDate", l_result.get("logDate"),
                is(equalTo("02/Apr/2020:08:08:28 +0200")));

        assertThat("We should have an entry for verb", l_result.containsKey("verb"));
        assertThat("We should have the correct value for logDate", l_result.get("verb"), is(equalTo("GET")));

        assertThat("We should have an entry for the API", l_result.containsKey("path"));
        assertThat("We should have the correct value for logDate", l_result.get("path"),
                is(equalTo("rest/head/workflow/WKF193")));
    }

    @Test
    public void testCreateApacheProfileFile()
            throws InstantiationException, IllegalAccessException, StringParseException {

        //Create a parse definition

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart(" /rest/head/");
        l_apiDefinition.setEnd(" ");

        ParseDefinition l_pDefinition = new ParseDefinition("SSL Log");
        l_pDefinition.setDefinitionEntries(Arrays.asList(l_verbDefinition2, l_apiDefinition));
        l_pDefinition.defineKeys(Arrays.asList(l_apiDefinition, l_verbDefinition2));

        final String apacheLogFile = "src/test/resources/logTests/apache/ssl_accessSmall.log";

        Map<String, GenericEntry> l_entries = StringParseFactory
                .extractLogEntryMap(Arrays.asList(apacheLogFile), l_pDefinition, GenericEntry.class);

        assertThat(l_entries, is(notNullValue()));
        assertThat("We should have entries", l_entries.size(), is(greaterThan(0)));
        assertThat("We should have entries", l_entries.size(), is(lessThan(19)));
        assertThat("We should have the key for amcDataSource",
                l_entries.containsKey("amcDataSource/AMCDS745177#GET"));

        for (GenericEntry lt_entry : l_entries.values()) {
            System.out.println(lt_entry.fetchPrintOut());
        }

    }

    @Test
    public void testCreateApacheProfileFile_Negative()
            throws InstantiationException, IllegalAccessException, StringParseException {

        //Create a parse definition

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        ParseDefinition l_pDefinition = new ParseDefinition("SSL Log");
        l_pDefinition.setDefinitionEntries(Arrays.asList(l_verbDefinition2));

        Map<String, GenericEntry> l_entries = StringParseFactory.extractLogEntryMap(Arrays.asList(),
                l_pDefinition, GenericEntry.class);

        assertThat(l_entries, is(notNullValue()));
        assertThat("We should have entries", l_entries.size(), is(equalTo(0)));

    }

    @Test
    public void testCreateApacheProfileFile_Negative2()
            throws InstantiationException, IllegalAccessException, StringParseException {

        //Create a parse definition

        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        ParseDefinition l_pDefinition = new ParseDefinition("SSL Log");
        l_pDefinition.setDefinitionEntries(Arrays.asList(l_verbDefinition2));

        final String apacheLogFile = "src/test/resources/logTests/apache/NonExistant.log";

        Map<String, GenericEntry> l_entries = StringParseFactory
                .extractLogEntryMap(Arrays.asList(apacheLogFile), l_pDefinition, GenericEntry.class);

        assertThat(l_entries, is(notNullValue()));
        assertThat("We should have entries", l_entries.size(), is(equalTo(0)));

    }

    @Test
    public void testStartPosition() {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        assertThat("We should have the correct start position",
                l_verbDefinition2.fetchStartPosition(l_apacheLogString), is(equalTo(83)));

        assertThat("We should have -1 for the start position", l_verbDefinition2.fetchStartPosition("   "),
                is(equalTo(-1)));

    }

    @Test
    public void testStartPositionCaseInsensitive() {
        String logString = "+69802:5ee7914c:15|POST /nl/jsp/soaprouter.jsp HTTP/1.1|X-Security-Token:@tTD6JQ5HcTcxBqbuE1SqEpNNsMbnjOCaV_Kv5ern7fvljTyUK71i9TWy5d6HBrFgCxfzCWt5OkJcJ_BfmC8mfw==|soapaction:xtk%3aqueryDef#ExecuteQuery|Content-Length:591|Content-Type:text/plain; charset=ISO-8859-1|Host:rd-dev54.rd.campaign.adobe.com|Connection:Keep-Alive|User-Agent:Apache-HttpClient/4.5.2 (Java/1.8.0_171)|Cookie:__sessiontoken=___6fffa340-b973-4e90-a965-4ae88e713c11|Accept-Encoding:gzip,deflate";

        //Create a parse definition
        ParseDefinitionEntry l_definitionCI = new ParseDefinitionEntry();

        l_definitionCI.setTitle("path");
        l_definitionCI.setStart("soapAction:");
        l_definitionCI.setEnd("#");
        l_definitionCI.setCaseSensitive(false);

        //Create a parse definition
        ParseDefinitionEntry l_definitionCS = new ParseDefinitionEntry();

        l_definitionCS.setTitle("path");
        l_definitionCS.setStart("soapaction:");
        l_definitionCS.setEnd("#");
        l_definitionCS.setCaseSensitive(true);

        assertThat("A start position should have been found", l_definitionCI.fetchStartPosition(logString),
                is(greaterThan(0)));

        assertThat("We should have the correct start position", l_definitionCS.fetchStartPosition(logString),
                is(equalTo(l_definitionCI.fetchStartPosition(logString))));
    }

    @Test
    public void testStartPositionCaseInsensitive_searchStringDifferentCase() {
        String logString = "t5OkJcJ_BfmC8mfw==|SOAPAction:xtk%3aqueryDef#ExecuteQuery|Content-Length:591|Content-Type:text/plain; ";

        //Create a parse definition
        ParseDefinitionEntry l_definitionCI = new ParseDefinitionEntry();

        l_definitionCI.setTitle("path");
        l_definitionCI.setStart("soapAction:");
        l_definitionCI.setEnd("#");
        l_definitionCI.setCaseSensitive(false);

        //Create a parse definition
        ParseDefinitionEntry l_definitionCS = new ParseDefinitionEntry();

        l_definitionCS.setTitle("path");
        l_definitionCS.setStart("SOAPAction:");
        l_definitionCS.setEnd("#");
        l_definitionCS.setCaseSensitive(true);

        assertThat("A start position should have been found", l_definitionCI.fetchStartPosition(logString),
                is(greaterThan(0)));

        assertThat("We should have the correct start position", l_definitionCS.fetchStartPosition(logString),
                is(equalTo(l_definitionCI.fetchStartPosition(logString))));
    }

    @Test
    public void testEndPosition() {
        String l_apacheLogString = "afthost32.qa.campaign.adobe.com:443 10.10.247.85 - - [02/Apr/2020:08:08:28 +0200] \"GET /rest/head/workflow/WKF193 HTTP/1.1\" 200 20951 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_242)\"";

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition2 = new ParseDefinitionEntry();

        l_verbDefinition2.setTitle("verb");
        l_verbDefinition2.setStart("\"");
        l_verbDefinition2.setEnd(" /");

        assertThat("We should have the correct start position",
                l_verbDefinition2.fetchEndPosition(l_apacheLogString), is(equalTo(86)));

        assertThat("We should have -1 for the start position", l_verbDefinition2.fetchStartPosition("   "),
                is(equalTo(-1)));

    }

    @Test
    public void testEndPositionCaseInsensitive() {
        String logString = "+69802:5ee7914c:15|POST /nl/jsp/soaprouter.jsp HTTP/1.1|X-Security-Token:@tTD6JQ5HcTcxBqbuE1SqEpNNsMbnjOCaV_Kv5ern7fvljTyUK71i9TWy5d6HBrFgCxfzCWt5OkJcJ_BfmC8mfw==|soapaction:xtk%3aqueryDef#ExecuteQuery|Content-Length:591|Content-Type:text/plain; charset=ISO-8859-1|Host:rd-dev54.rd.campaign.adobe.com|Connection:Keep-Alive|User-Agent:Apache-HttpClient/4.5.2 (Java/1.8.0_171)|Cookie:__sessiontoken=___6fffa340-b973-4e90-a965-4ae88e713c11|Accept-Encoding:gzip,deflate";

        //Create a parse definition
        ParseDefinitionEntry l_definitionCI = new ParseDefinitionEntry();

        l_definitionCI.setTitle("path");
        l_definitionCI.setStart("X-Security-Token");
        l_definitionCI.setEnd("soapAction:");

        l_definitionCI.setCaseSensitive(false);

        //Create a parse definition
        ParseDefinitionEntry l_definitionCS = new ParseDefinitionEntry();

        l_definitionCS.setTitle("path");
        l_definitionCS.setStart("X-Security-Token");
        l_definitionCS.setEnd("soapaction:");
        l_definitionCS.setCaseSensitive(true);

        assertThat("An end position should have been found", l_definitionCI.fetchEndPosition(logString),
                is(greaterThan(0)));

        assertThat("We should have the correct start position", l_definitionCS.fetchEndPosition(logString),
                is(equalTo(l_definitionCI.fetchEndPosition(logString))));
    }

    @Test
    public void testEndPositionCaseInsensitive_searchStringDifferentCase() {
        String logString = "HTTP/1.1|X-Security-Token:@tTD6JQ5HcTfzCWt5OkJcJ_BfmC8mfw==|SOAPAction:xtk%3aqueryDef#ExecuteQuery|Content-Length:591|";

        //Create a parse definition
        ParseDefinitionEntry l_definitionCI = new ParseDefinitionEntry();

        l_definitionCI.setTitle("path");
        l_definitionCI.setStart("X-Security-Token");
        l_definitionCI.setEnd("soapAction:");
        l_definitionCI.setCaseSensitive(false);

        //Create a parse definition
        ParseDefinitionEntry l_definitionCS = new ParseDefinitionEntry();

        l_definitionCS.setTitle("path");
        l_definitionCS.setStart("X-Security-Token");
        l_definitionCS.setEnd("SOAPAction:");
        l_definitionCS.setCaseSensitive(true);

        assertThat("An end position should have been found", l_definitionCI.fetchEndPosition(logString),
                is(greaterThan(0)));

        assertThat("We should have the correct start position", l_definitionCS.fetchEndPosition(logString),
                is(equalTo(l_definitionCI.fetchEndPosition(logString))));
    }

    @Test
    public void testApplySensitivity() {
        //Create a parse definition Case Insensitive
        ParseDefinitionEntry l_definitionCI = new ParseDefinitionEntry();

        l_definitionCI.setTitle("path");
        l_definitionCI.setStart("X-Security-Token");

        final String l_targetString = "soapAction:";
        l_definitionCI.setEnd(l_targetString);

        l_definitionCI.setCaseSensitive(false);

        //Create a parse definition Case Sensitive
        ParseDefinitionEntry l_definitionCS = new ParseDefinitionEntry();

        l_definitionCS.setTitle("path");
        l_definitionCI.setStart("X-Security-Token");
        l_definitionCS.setEnd(l_targetString);
        l_definitionCS.setCaseSensitive(true);

        assertThat("The apply sensitivity for CaseInsensitive failed",
                l_definitionCI.fetchAppliedSensitivity(l_definitionCI.getEnd()),
                is(equalTo(l_targetString.toLowerCase())));
    }

    @Test
    public void testNextString() {
        String l_apacheLogString = "2330:DEBUG | 2020-04-03 17:46:38 | [main] core.NextTests (NextTests.java:209) - Before driver instantiation";

        //Create a parse definition
        ParseDefinitionEntry l_fileDefinition = new ParseDefinitionEntry();

        l_fileDefinition.setTitle("sourceFile");
        l_fileDefinition.setStart("(");
        l_fileDefinition.setEnd(":");

        assertThat("Testing that we can correctly fetch the following substring",
                l_fileDefinition.fetchFollowingSubstring(l_apacheLogString),
                is(equalTo(":209) - Before driver instantiation")));

    }

    @Test(description = "This test should fail because no end string can ve found")
    public void testNextStringNegative() {
        String l_apacheLogString = "2330:DEBUG | 2020-";

        //Create a parse definition
        ParseDefinitionEntry l_fileDefinition = new ParseDefinitionEntry();

        l_fileDefinition.setTitle("sourceFile");
        l_fileDefinition.setStart(":");
        l_fileDefinition.setEnd("9");

        assertThrows(IllegalArgumentException.class,
                () -> l_fileDefinition.fetchFollowingSubstring(l_apacheLogString));

    }

    @Test(description = "Fixing bug where the patterns could be used elsewhere in the line")
    public void testFetchValueDuplicatedStartEndPatterns() throws StringParseException {
        String l_resultString = "2330:DEBUG | 2020-04-03 17:46:38 | [main] core.NextTests (NextTests.java:209) - Before driver instantiation";

        //Create a parse definition

        ParseDefinitionEntry l_fileDefinition = new ParseDefinitionEntry();

        l_fileDefinition.setTitle("sourceFile");
        l_fileDefinition.setStart("(");
        l_fileDefinition.setEnd(":");

        ParseDefinitionEntry l_fileLineDefinition = new ParseDefinitionEntry();

        l_fileLineDefinition.setTitle("sourceFileLine");
        l_fileLineDefinition.setStart(":");
        l_fileLineDefinition.setEnd(")");

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_fileDefinition);
        l_definitionList.add(l_fileLineDefinition);
        Map<String, String> l_result = StringParseFactory.parseString(l_resultString, l_definitionList);

        assertThat("We should have a result", l_result, is(notNullValue()));

        assertThat("We should have two entries", l_result.size(), is(equalTo(2)));

        assertThat("We should have an entry for sourceFile", l_result.containsKey("sourceFile"));
        assertThat("We should have the correct value for sourceFile", l_result.get("sourceFile"),
                is(equalTo("NextTests.java")));

        assertThat("We should have an entry for sourceFileLine", l_result.containsKey("sourceFileLine"));
        assertThat("We should have the correct value for sourceFileLine", l_result.get("sourceFileLine"),
                is(equalTo("209")));

    }

    /////////////////////////  RESULT PUBLISHER  ///////////////////////// 

    @Test
    public void testIncrementation() {
        GenericEntry l_entry = new GenericEntry();

        assertThat("Initial value of occurences should be 1", l_entry.getFrequence(), is(equalTo(1)));
        l_entry.incrementUsage();
        assertThat(l_entry.getFrequence(), is(equalTo(2)));

        l_entry.addFrequence(3);
        assertThat(l_entry.getFrequence(), is(equalTo(5)));
    }

    @Test
    public void testProblemContentsWithStringsAroundDefault() throws StringParseException {
        String logString = "SOAPAction:\"xtk%3aworkflow\"#DeleteResult|Content-Length:";

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart("soapaction:");
        l_apiDefinition.setEnd("#");
        l_apiDefinition.setCaseSensitive(false);

        assertThat("We should be default remove trailing quotes", !l_apiDefinition.isTrimQuotes());

        assertThat("", l_apiDefinition.fetchStartPosition(logString), is(equalTo(11)));

        assertThat("", l_apiDefinition.fetchEndPosition(logString), is(equalTo(27)));

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_apiDefinition);

        assertThat("The String should be compatible",
                StringParseFactory.isStringCompliant(logString, l_definitionList));

        assertThat("We should have found the correct path",
                StringParseFactory.fetchValue(logString, l_apiDefinition), is(equalTo("\"xtk%3aworkflow\"")));

        Map<String, String> l_entries = StringParseFactory.parseString(logString, l_definitionList);

        assertThat("We should have the correct value for api", l_entries.get("path"),
                is(equalTo("\"xtk%3aworkflow\"")));
    }

    @Test
    public void testProblemContentsWithStringsAroundTrimmed() throws StringParseException {
        String logString = "SOAPAction:\"xtk%3aworkflow\"#DeleteResult|Content-Length:";

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart("soapaction:");
        l_apiDefinition.setEnd("#");
        l_apiDefinition.setCaseSensitive(false);

        l_apiDefinition.setTrimQuotes(true);
        assertThat("We should be default remove trailing quotes", l_apiDefinition.isTrimQuotes());

        assertThat("", l_apiDefinition.fetchStartPosition(logString), is(equalTo(12)));

        assertThat("", l_apiDefinition.fetchEndPosition(logString), is(equalTo(26)));

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_apiDefinition);

        assertThat("The String should be compatible",
                StringParseFactory.isStringCompliant(logString, l_definitionList));

        assertThat("We should have found the correct path",
                StringParseFactory.fetchValue(logString, l_apiDefinition), is(equalTo("xtk%3aworkflow")));

        Map<String, String> l_entries = StringParseFactory.parseString(logString, l_definitionList);

        assertThat("We should have the correct value for api", l_entries.get("path"),
                is(equalTo("xtk%3aworkflow")));
    }

    @Test
    public void testProblemTrimming() {
        String logString = "DEBUG | 2020-06-18 16:06:44 | [main] parser.StringParseFactory (StringParseFactory.java:47) - 46472  -  +29043:5eeae354:3a5b|POST /nl/jsp/soaprouter.jsp HTTP/1.1|soapaction:atdvhwetsjlcafl|Content-Length:384|Content-Type:text/plain; charset=ISO-8859-1|Host:rd-dev59.rd.campaign.adobe.com|Connection:Keep-Alive|User-Agent:Apache-HttpClient/4.5.2 (Java/1.8.0_171)|Accept-Encoding:gzip,deflate";

        //Create a parse definition
        ParseDefinitionEntry l_apiDefinition = new ParseDefinitionEntry();

        l_apiDefinition.setTitle("path");
        l_apiDefinition.setStart("soapaction:");
        l_apiDefinition.setEnd("#");
        l_apiDefinition.setCaseSensitive(false);

        l_apiDefinition.setTrimQuotes(true);

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_apiDefinition);

        assertThat("The String should not be compatible",
                !StringParseFactory.isStringCompliant(logString, l_definitionList));

    }

    @Test
    public void testToPreserve() throws InstantiationException, IllegalAccessException, StringParseException {

        ParseDefinitionEntry l_lineFinder = new ParseDefinitionEntry();

        l_lineFinder.setTitle("lineBeginning");
        l_lineFinder.setStart("<soapCall");
        l_lineFinder.setEnd(" ");
        l_lineFinder.setCaseSensitive(false);
        l_lineFinder.setToPreserve(false);

        ParseDefinitionEntry l_verb = new ParseDefinitionEntry();

        l_verb.setTitle("verb");
        l_verb.setStart("name=\"");
        l_verb.setEnd("\"");
        l_verb.setCaseSensitive(false);

        assertThat("We would by default be preserving entries", l_verb.isToPreserve());

        ParseDefinitionEntry l_srcSchema = new ParseDefinitionEntry();

        l_srcSchema.setTitle("path");
        l_srcSchema.setStart("service=\"");
        l_srcSchema.setEnd("\"");
        l_srcSchema.setCaseSensitive(false);

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_lineFinder);
        l_definitionList.add(l_verb);
        l_definitionList.add(l_srcSchema);

        //Check that the definition is correct 
        String l_line = "                <soapCall name=\"resetServiceMobileAppsCustomHook\" service=\"nms:mobileApp\">";
        Map<String, String> l_parseResult = StringParseFactory.parseString(l_line, l_definitionList);
        assertThat("The given log should be compliant",
                StringParseFactory.isStringCompliant(l_line, l_definitionList));
        assertThat("We should have values", l_parseResult.size(), is(equalTo(3)));
    }

}
