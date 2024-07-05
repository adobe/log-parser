package com.adobe.campaign.tests.logparser.exceptions;

public class LogParserPostManipulationException
        extends RuntimeException {
    public LogParserPostManipulationException(String message, Exception e) {
        super(message, e);
    }
}
