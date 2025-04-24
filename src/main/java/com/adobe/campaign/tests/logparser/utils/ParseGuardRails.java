/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to store guard rails for parsing operations
 */
public class ParseGuardRails {
    public static final Map<String, Long> fileSizeLimitations = new HashMap<>();
    public static long HEAP_SIZE_AT_START = MemoryUtils.getCurrentHeapSizeMB();
    public static int FILE_ENTRY_LIMIT = Integer.parseInt(System.getProperty("PROP_LOGPARSER_FILEENTRY_LIMIT", "-1"));
    public static long HEAP_LIMIT = Integer.parseInt(System.getProperty("PROP_LOGPARSER_HEAP_LIMIT", "-1"));
    public static double MEMORY_LIMIT_PERCENTAGE = Double
            .parseDouble(System.getProperty("PROP_LOGPARSER_MEMORY_LIMIT_PERCENTAGE", "-1"));

    public static void reset() {
        fileSizeLimitations.clear();
        FILE_ENTRY_LIMIT = -1;
        HEAP_LIMIT = -1;
        MEMORY_LIMIT_PERCENTAGE = -1;
    }

    /**
     * Check if the current count has reached the entry limit
     * 
     * @param currentCount the current count of entries
     * @return true if the current count has reached the entry limit, false
     *         otherwise
     */
    public static boolean hasReachedEntryLimit(int currentCount) {
        return FILE_ENTRY_LIMIT > -1 && currentCount >= FILE_ENTRY_LIMIT;
    }

    /**
     * Check if the current memory usage has reached the memory limit
     * 
     * @return true if the current memory usage has reached the memory limit, false
     *         otherwise
     */
    public static boolean hasReachedHeapLimit() {
        return HEAP_LIMIT > -1 && (MemoryUtils.getCurrentHeapSizeMB() - HEAP_SIZE_AT_START) >= HEAP_LIMIT;
    }

    /**
     * Check if the remaining memory percentage has fallen below the set limit
     * 
     * @return true if the remaining memory percentage is below the limit, false
     *         otherwise
     */
    public static boolean hasReachedMemoryPercentageLimit() {
        return MEMORY_LIMIT_PERCENTAGE > -1 && MemoryUtils.getRemainingMemoryPercentage() <= MEMORY_LIMIT_PERCENTAGE;
    }
}