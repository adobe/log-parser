/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.exceptions;

/**
 * Exceptions for managing problems with post manipulation of the log data
 */
public class LogParserPostManipulationException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LogParserPostManipulationException(String message, Exception e) {
        super(message, e);
    }
}
