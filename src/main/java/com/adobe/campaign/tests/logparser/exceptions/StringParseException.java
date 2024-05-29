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
 * This Exception covers all exceptions related to parsing of String using the
 * log-parser logic
 *
 *
 * Author : gandomi
 *
 */
public class StringParseException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1604668750865904244L;

    public StringParseException(String in_message) {
        super(in_message);
    }

}
