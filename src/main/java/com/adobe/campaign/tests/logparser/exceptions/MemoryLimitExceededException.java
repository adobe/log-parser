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
 * Exception thrown when memory limits are exceeded
 */
public class MemoryLimitExceededException extends RuntimeException {

    public MemoryLimitExceededException(String message) {
        super(message);
    }
}