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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.contains;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.adobe.campaign.tests.logparser.GenericEntry;
import com.adobe.campaign.tests.logparser.ParseDefinitionEntry;
import com.adobe.campaign.tests.logparser.StringParseFactory;



public class TestSimpleLog {
    
    @Test
    public void testSimpleLog() {
       
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
            assertThat("We should have entries", l_searchMaps.get("timeStamp"), is(equalTo("2020-06-15T17:17:20.728Z")));

        
    }

    
    @Test
    public void testParseDefinition() {
       
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

            assertThat("We should have the same entries", l_parseDefinition.getDefinitionEntries().size(),is(equalTo(3)));


            Map<String, String> l_searchMaps = StringParseFactory.parseString(logString, l_parseDefinition.getDefinitionEntries());
            

            assertThat(l_searchMaps.size(), is(equalTo(3)));
            assertThat("We should have entries", l_searchMaps.get("timeStamp"), is(equalTo("2020-06-15T17:17:20.728Z")));

    }
    
    @Test
    public void testParseDefinition2() {
       
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

            assertThat("We should have the same entries", l_parseDefinition.getDefinitionEntries().size(),is(equalTo(3)));


            Map<String, String> l_searchMaps = StringParseFactory.parseString(logString, l_parseDefinition);
            

            assertThat(l_searchMaps.size(), is(equalTo(3)));
            assertThat("We should have entries", l_searchMaps.get("timeStamp"), is(equalTo("2020-06-15T17:17:20.728Z")));

    }
    
    @Test
    public void testIssueWithTracking() {
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
    public void testFetchDateStringSTDMETHOD() throws ParseException {
        String l_resultString = "2330:DEBUG | 2020-04-03 17:46:38 | [main] core.NextTests (NextTests.java:209) - Before driver instantiation";
        
        //Create a parse definition
        ParseDefinitionEntry l_definition = new ParseDefinitionEntry();

        l_definition.setTitle("LogLevel");
        l_definition.setStart(" | ");
        l_definition.setEnd(" | ");

        assertThat(StringParseFactory.fetchValue(l_resultString, l_definition), is(equalTo("2020-04-03 17:46:38")));

    }
    
    @Test(description = "Checking that we correctly identify the line in the file source")
    public void testFetchFileLineSTDMETHOD () {
        String l_resultString = "2330:DEBUG | 2020-04-03 17:46:38 | [main] core.NextTests (NextTests.java:209) - Before driver instantiation";

      //Create a parse definition
        ParseDefinitionEntry l_definition = new ParseDefinitionEntry();

        l_definition.setTitle("LogLevel");
        l_definition.setStart("a:");
        l_definition.setEnd(")");

        assertThat(StringParseFactory.fetchValue(l_resultString, l_definition), is(equalTo("209")));

    }
    
    
    @Test(description = "Checking that we correctly identify the file source")
    public void testFetchFileSTDMETHOD () {
        String l_resultString = "2330:DEBUG | 2020-04-03 17:46:38 | [main] core.NextTests (NextTests.java:209) - Before driver instantiation";

      //Create a parse definition
        ParseDefinitionEntry l_definition = new ParseDefinitionEntry();

        l_definition.setTitle("LogLevel");
        l_definition.setStart("(");
        l_definition.setEnd(":");

        assertThat(StringParseFactory.fetchValue(l_resultString, l_definition), is(equalTo("NextTests.java")));

    }
    
    

    
    
    /******** Imported tests ; Harnesses *********/
    @Test
    public void testFetchACCCoverageFromFile() throws InstantiationException, IllegalAccessException {

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

        List<ParseDefinitionEntry> l_definitionList = new ArrayList<>();
        l_definitionList.add(l_apiDefinition);
        l_definitionList.add(l_verbDefinition);

        final String apacheLogFile = "src/test/resources/logTests/acc/acc_integro_jenkins_log_exerpt.txt";

        Map<String, GenericEntry> l_entries = StringParseFactory
                .fetchLogData(Arrays.asList(apacheLogFile), l_definitionList, GenericEntry.class);

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
    public void testItems() {
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
    
}
