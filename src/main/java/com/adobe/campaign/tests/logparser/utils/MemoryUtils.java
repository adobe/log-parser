/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

/**
 * Utility class for memory-related operations
 */
public class MemoryUtils {

    /**
     * Gets the current heap size in megabytes
     * 
     * @return the current heap size in MB
     */
    public static long getCurrentHeapSizeMB() {
        return Runtime.getRuntime().totalMemory() / (1024 * 1024);
    }

    /**
     * Gets the occupied system memory as a percentage
     * 
     * @return the occupied system memory as a percentage (0-100)
     */
    public static double getUsedMemoryPercentage() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = allocatedMemory - freeMemory;
        return ((double) usedMemory / maxMemory) * 100;
    }
}