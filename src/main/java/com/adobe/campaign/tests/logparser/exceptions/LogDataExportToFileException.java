/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.exceptions;

import java.io.IOException;

/**
 * Exceptions for managing problems with import and export
 */
public class LogDataExportToFileException extends Exception {

    public LogDataExportToFileException(String s, Exception ex) {
        super(s, ex);
    }

    public LogDataExportToFileException(String s) {
        super(s);
    }
}
