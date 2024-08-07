/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.exceptions;

public class LogParserSDKDefinitionException extends RuntimeException {

        /**
        *
        */
        private static final long serialVersionUID = 8447982808632053227L;

        public LogParserSDKDefinitionException(String string, Exception e) {
            super(string, e);
        }

}
