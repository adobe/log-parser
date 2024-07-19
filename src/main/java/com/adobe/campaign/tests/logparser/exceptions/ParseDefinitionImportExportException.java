/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.exceptions;

public class ParseDefinitionImportExportException extends RuntimeException {

    /**
     * This exceptions is to be thrown whenever we have a problem instantiatioting one of the extensions of StdLogParser
     */
    private static final long serialVersionUID = 8447982808632053227L;

    public ParseDefinitionImportExportException(String string, Exception e) {
        super(string, e);
    }

    public ParseDefinitionImportExportException(String string) {
        super(string);
    }

}
