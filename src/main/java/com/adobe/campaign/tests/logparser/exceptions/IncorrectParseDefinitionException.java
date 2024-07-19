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
 * Thrown when there is an incompatibility between stored values and d keys comprising the Entry key
 */
public class IncorrectParseDefinitionException extends Exception {

   private static final long serialVersionUID = 6462690423575172063L;

    /**
     * The exception thrown when there is a problem in the parse definition
     * @param in_message A message to be presented to the user
     */
    public IncorrectParseDefinitionException(String in_message) {
        super(in_message);
    }

}
