/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.hamcrest.Matchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.adobe.campaign.tests.logparser.exceptions.ParseDefinitionImportExportException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseDefinitionTests {

    @BeforeMethod
    public void cleanUp() {
        File l_stdOutPutFile = new File(LogParser.OUTPUT_DIR);

        if (l_stdOutPutFile.exists()) {
            for (File lt_file : l_stdOutPutFile.listFiles()) {
                assertTrue(lt_file.delete());
            }
            assertTrue(l_stdOutPutFile.delete());
        }

    }

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

        assertThat("The created parse definitions should be the same as the old one", l_parseDefinition,
                equalTo(l_newParseDefinition));

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

    @Test
    public void testImportExportToJSON() throws ParseDefinitionImportExportException {

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

        final String l_jsonPath = LogParser.OUTPUT_DIR + "/firstDefinition.json";

        File l_storedJSON = ParseDefinitionFactory.exportParseDefinitionToJSON(l_parseDefinition, l_jsonPath);

        ParseDefinition fetchedJSON = ParseDefinitionFactory
                .importParseDefinition(l_storedJSON.getAbsolutePath());

        assertThat("Both `parseDefinitions should be the same", fetchedJSON, equalTo(l_parseDefinition));
    }

    @Test
    public void testImportExportToJSON_withFileExport() throws ParseDefinitionImportExportException {

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
        l_parseDefinition.setStoreFilePath(true);
        l_parseDefinition.setStoreFileName(true);

        final String l_jsonPath = LogParser.OUTPUT_DIR + "/firstDefinitionWithFilePath.json";

        File l_storedJSON = ParseDefinitionFactory.exportParseDefinitionToJSON(l_parseDefinition, l_jsonPath);

        ParseDefinition fetchedJSON = ParseDefinitionFactory
                .importParseDefinition(l_storedJSON.getAbsolutePath());

        assertThat("Both `parseDefinitions should be the same", fetchedJSON, equalTo(l_parseDefinition));
    }

    @Test
    public void testImportExportToJSONKeyOrder() throws ParseDefinitionImportExportException {

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

        l_parseDefinition.defineKeys(l_apiDefinition);

        final String l_jsonPath = LogParser.OUTPUT_DIR + "/keyOrderDefinition.json";

        File l_storedJSON = ParseDefinitionFactory.exportParseDefinitionToJSON(l_parseDefinition, l_jsonPath);

        ParseDefinition fetchedJSON = ParseDefinitionFactory
                .importParseDefinition(l_storedJSON.getAbsolutePath());

        assertThat("Both `parseDefinitions should be the same", fetchedJSON, equalTo(l_parseDefinition));
        assertThat("The keyOrder should be present", fetchedJSON.getKeyOrder(),
                Matchers.contains(l_apiDefinition.getTitle()));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testImportExportToJSON_negative() throws ParseDefinitionImportExportException,
            JsonGenerationException, IOException {
        ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
        Mockito.doThrow(IOException.class).when(mapper).writeValue(ArgumentMatchers.any(File.class),
                ArgumentMatchers.any(ParseDefinition.class));

        Mockito.doThrow(IOException.class).when(mapper).readValue(ArgumentMatchers.any(File.class),
                ArgumentMatchers.any(Class.class));

        //Create a parse definition
        ParseDefinitionEntry l_verbDefinition = new ParseDefinitionEntry();

        l_verbDefinition.setTitle("verb");
        l_verbDefinition.setStart("\"");
        l_verbDefinition.setEnd(" /");

        ParseDefinition l_parseDefinition = new ParseDefinition("rest calls");
        l_parseDefinition.addEntry(l_verbDefinition);

        final String l_jsonPath = LogParser.OUTPUT_DIR + "/firstDefinition.json";

        Assert.assertThrows(ParseDefinitionImportExportException.class, () -> ParseDefinitionFactory
                .exportParseDefinitionToJSON(l_parseDefinition, new File(l_jsonPath), mapper));

        Assert.assertThrows(ParseDefinitionImportExportException.class,
                () -> ParseDefinitionFactory.importParseDefinition(new File(l_jsonPath), mapper));

    }

    //// Utils

    @Test
    public void testCreateParents() {
        File parentDir = new File(LogParser.OUTPUT_DIR);

        parentDir.delete();
        File l_jsonPath = new File(LogParser.OUTPUT_DIR + "/random.json");

        assertThat("The parent dir should not exist", !l_jsonPath.getParentFile().exists());

        ParseDefinitionFactory.createParents(l_jsonPath);

        assertThat("The parent dir should now exist", l_jsonPath.getParentFile().exists());

        //The second execution should happen with no problems.
        ParseDefinitionFactory.createParents(l_jsonPath);

        assertThat("The parent dir should now exist", l_jsonPath.getParentFile().exists());

        //No Parent Dir
        File l_jsonPath2 = new File("random2.json");
        ParseDefinitionFactory.createParents(l_jsonPath2);

    }

    @Test
    public void testImportJSON() throws ParseDefinitionImportExportException {

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

        l_parseDefinition.defineKeys(l_apiDefinition);

        final String l_jsonPath = "src/test/resources/parseDefinitions/myParseDefinition.json";

        ParseDefinition l_fetchedJSON = ParseDefinitionFactory.importParseDefinition(l_jsonPath);

        assertThat("Both `parseDefinitions should be the same", l_fetchedJSON, equalTo(l_parseDefinition));

        assertThat("The original key order should not be null", l_parseDefinition.fetchKeyOrder(),
                Matchers.notNullValue());
        assertThat("By default, if not set we use all entries as key",
                l_parseDefinition.fetchKeyOrder().size(), is(1));

        assertThat("The new key order should not be null", l_fetchedJSON.fetchKeyOrder(),
                Matchers.notNullValue());
        assertThat("The new key order should be empty", l_fetchedJSON.fetchKeyOrder(),
                is(equalTo(l_parseDefinition.fetchKeyOrder())));

    }

    @Test
    public void testImportJSONNegative_NonExistant() throws ParseDefinitionImportExportException {

        final String l_jsonPath = "src/test/resources/parseDefinitions/myParseDefinitionNONExistant.json";

        assertThrows(ParseDefinitionImportExportException.class,
                () -> ParseDefinitionFactory.importParseDefinition(l_jsonPath));

    }

    @Test
    public void testImportJSONNegative_NotAJSON() throws ParseDefinitionImportExportException {

        final String l_jsonPath = "src/test/resources/parseDefinitions/myBadParseDefinition.json";

        assertThrows(ParseDefinitionImportExportException.class,
                () -> ParseDefinitionFactory.importParseDefinition(l_jsonPath));

    }
    
    @Test
    public void testImportJSONNegative_BadKeyInKeyOrder() throws ParseDefinitionImportExportException {

        final String l_jsonPath = "src/test/resources/parseDefinitions/myParseDefinitionBadKeyOrder.json";

        assertThrows(ParseDefinitionImportExportException.class,
                () -> ParseDefinitionFactory.importParseDefinition(l_jsonPath));
    }

    @Test
    public void testParseTitle() {
        ParseDefinition l_p1 = new ParseDefinition("Title 1");
        assertThat("The file generation of the title should be correct", l_p1.fetchEscapedTitle(), Matchers.equalTo("Title-1"));

        l_p1.setTitle(" ");
        assertThat("Emptt tutles should have a default value", l_p1.fetchEscapedTitle(), Matchers.equalTo("parseDefinitionResult"));

        l_p1.setTitle("title 3 ");
        assertThat("The file generation of the title should be correct", l_p1.fetchEscapedTitle(), Matchers.equalTo("title-3"));

    }
    
}
