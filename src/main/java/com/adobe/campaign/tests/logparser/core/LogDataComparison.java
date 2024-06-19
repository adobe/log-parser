/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.core;

public class LogDataComparison<T extends StdLogEntry> {
    private final T logEntry;
    private final ChangeType changeType;
    private Integer delta;
    private Double deltaRatio;

    public LogDataComparison(T in_LogEntry, ChangeType in_changeType, Integer in_originalFrequence, int in_newFrequence) {
        this.logEntry = in_LogEntry;
        this.changeType = in_changeType;
        this.delta = in_newFrequence - in_originalFrequence;
        this.deltaRatio = Math.signum(this.delta) * ((in_newFrequence*in_originalFrequence==0) ? 1 : (double) in_originalFrequence / in_newFrequence);
    }

    public T getLogEntry() {
        return logEntry;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public Integer getDelta() {
        return delta;
    }


    public Double getDeltaRatio() {
        return deltaRatio;
    }


    enum ChangeType {
        ADDED, REMOVED, MODIFIED
    };
}
