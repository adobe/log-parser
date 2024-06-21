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

public class LogParserFileUtils {
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
}
