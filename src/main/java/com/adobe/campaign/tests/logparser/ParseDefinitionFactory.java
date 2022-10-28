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

import java.io.File;
import java.io.IOException;

import com.adobe.campaign.tests.logparser.exceptions.ParseDefinitionImportExportException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseDefinitionFactory {

    /**
     * Imports a ParseDefinition declared in a json file
     *
     * Author : gandomi
     *
     * @param in_jsonParseDefinitionFilePath
     *        The file path of a ParseDefinition
     * @return A Parse Definition
     * @throws ParseDefinitionImportExportException
     *         Whenever we encounter problem while parsing the file
     *
     */
    public static ParseDefinition importParseDefinition(final String in_jsonParseDefinitionFilePath)
            throws ParseDefinitionImportExportException {

        return importParseDefinition(new File(in_jsonParseDefinitionFilePath));
    }

    /**
     * Imports a ParseDefinition declared in a json file
     *
     * Author : gandomi
     *
     * @param in_jsonFile
     *        The file path of a ParseDefinition
     * @return A Parse Definition
     * @throws ParseDefinitionImportExportException
     *         Whenever we encounter problem while parsing the file
     *
     */
    protected static ParseDefinition importParseDefinition(final File in_jsonFile)
            throws ParseDefinitionImportExportException {
        ObjectMapper mapper = new ObjectMapper();

        return importParseDefinition(in_jsonFile, mapper);
    }

    /**
     * Imports a ParseDefinition declared in a json file
     *
     * Author : gandomi
     *
     * @param in_jsonFile
     *        The file path of a ParseDefinition
     * @param mapper
     *        a mapper object for testing
     * @return A Parse Definition
     * @throws ParseDefinitionImportExportException
     *         Whenever we encounter problem while parsing the file
     *
     */
    public static ParseDefinition importParseDefinition(final File in_jsonFile, ObjectMapper mapper)
            throws ParseDefinitionImportExportException {

        if (!in_jsonFile.exists()) {
            throw new ParseDefinitionImportExportException(
                    "The provided json file " + in_jsonFile.getPath() + " does not exist.");
        }

        //Read JSON
        ParseDefinition fetchedFromJSON = new ParseDefinition();
        try {
            fetchedFromJSON = mapper.readValue(in_jsonFile, ParseDefinition.class);

            for (String lt_key : fetchedFromJSON.fetchKeyOrder()) {
                if (!fetchedFromJSON.fetchHeaders().contains(lt_key)) {
                    throw new ParseDefinitionImportExportException("The key " + lt_key
                            + " in keyOrder does not correspond to any known parse definition entries.");
                }

            }
        } catch (IOException e) {
            throw new ParseDefinitionImportExportException("Error when importing json file " + in_jsonFile,
                    e);
        }
        return fetchedFromJSON;
    }

    /**
     * Exports a ParseDefinition to a JSON file
     * 
     * Author : gandomi
     *
     * @param in_parseDefinition
     *        A ParseDefinition object
     * @param in_jsonFilePath
     *        A file path for storing the ParseDefinition
     * @return The file containing the exported data
     * @throws ParseDefinitionImportExportException
     *         when we face problems managing the export
     *
     */
    public static File exportParseDefinitionToJSON(ParseDefinition in_parseDefinition,
            final String in_jsonFilePath) throws ParseDefinitionImportExportException {

        return exportParseDefinitionToJSON(in_parseDefinition, new File(in_jsonFilePath));

    }

    /**
     * Exports a ParseDefinition to a JSON file
     * 
     * Author : gandomi
     *
     * @param in_parseDefinition
     *        A ParseDefinition object
     * @param in_jsonFile
     *        A file path for storing the ParseDefinition
     * @return The file containing the exported data
     * @throws ParseDefinitionImportExportException
     *         when we face problems managing the export
     *
     */
    public static File exportParseDefinitionToJSON(ParseDefinition in_parseDefinition, File in_jsonFile)
            throws ParseDefinitionImportExportException {
        ObjectMapper mapper = new ObjectMapper();

        createParents(in_jsonFile);

        return exportParseDefinitionToJSON(in_parseDefinition, in_jsonFile, mapper);

    }

    /**
     * A util method for ensuring the the parent directories
     *
     * Author : gandomi
     *
     * @param in_file
     *        A file that may or may not exist
     *
     */
    protected static void createParents(File in_file) {
        //Make sure that the parent directories exist
        if (in_file.getParentFile() != null && !in_file.getParentFile().exists()) {
            in_file.getParentFile().mkdirs();
        }
    }

    /**
     * Exports a ParseDefinition to a JSON file
     * 
     * Author : gandomi
     *
     * @param in_parseDefinition
     *        A ParseDefinition object
     * @param in_jsonFile
     *        A file path for storing the ParseDefinition
     * @param mapper
     *        a mapper object for testing
     * @return The file containing the exported data
     * @throws ParseDefinitionImportExportException
     *         when we face problems managing the export
     *
     */
    protected static File exportParseDefinitionToJSON(ParseDefinition in_parseDefinition, File in_jsonFile,
            ObjectMapper mapper) throws ParseDefinitionImportExportException {
        try {
            mapper.writeValue(in_jsonFile, in_parseDefinition);

        } catch (IOException e) {
            throw new ParseDefinitionImportExportException(
                    "Error while exporting parse definition to file " + in_jsonFile.getPath(), e);
        }

        return in_jsonFile;
    }

}
