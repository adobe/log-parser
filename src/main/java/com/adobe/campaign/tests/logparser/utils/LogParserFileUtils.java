/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */

package com.adobe.campaign.tests.logparser.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LogParserFileUtils {
    public static final String LOG_PARSER_EXPORTS = "log_parser_output/exports";

    protected static Logger log = LogManager.getLogger();

    /**
     * Deletes a file if it exists
     * @param in_file a file to delete
     */
    public static void cleanFile(File in_file) {
        if (in_file.exists() && in_file.delete()) {
            log.info("Deleting existing log export file {}...", in_file.getPath());
        }
    }

    /**
     * Creates a file in the standard location
     * @param in_fileName a file name
     * @return the file object
     */
    public static File createFile(String in_fileName) throws IOException {
        Files.createDirectories(Paths.get(LOG_PARSER_EXPORTS));
        return new File(LOG_PARSER_EXPORTS,in_fileName);
    }

    /**
     * Creates a file and ensuring that it is totally new by deleting it if it exists. The files are created under the folder log_parser_output/exports
     * @param in_fileName A file name
     * @return a file object which is completely new and empty
     */
    public static File createNewFile(String in_fileName) throws IOException {
        File lr_file = createFile(in_fileName);
        cleanFile(lr_file);
        return lr_file;
    }
}
