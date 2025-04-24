/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.adobe.campaign.tests.logparser.exceptions.MemoryLimitExceededException;

/**
 * Class to store guard rails for parsing operations
 */
public class ParseGuardRails {
    private static final Logger log = LogManager.getLogger();
    public static final Map<String, Long> fileSizeLimitations = new HashMap<>();
    public static final Map<String, Long> entryLimitations = new HashMap<>();

    public static long HEAP_SIZE_AT_START = MemoryUtils.getCurrentHeapSizeMB();
    public static int FILE_ENTRY_LIMIT = Integer.parseInt(System.getProperty("PROP_LOGPARSER_FILEENTRY_LIMIT", "-1"));
    public static long HEAP_LIMIT = Integer.parseInt(System.getProperty("PROP_LOGPARSER_HEAP_LIMIT", "-1"));
    public static double MEMORY_LIMIT_PERCENTAGE = Double
            .parseDouble(System.getProperty("PROP_LOGPARSER_MEMORY_LIMIT_PERCENTAGE", "-1"));
    protected static boolean EXCEPTION_ON_MEMORY_LIMIT = Boolean
            .parseBoolean(System.getProperty("PROP_LOGPARSER_EXCEPTION_ON_MEMORY_LIMIT", "false"));

    protected static long FILE_SIZE_LIMIT = Long
            .parseLong(System.getProperty("PROP_LOGPARSER_FILESIZE_LIMIT", "-1"));
    protected static int MEASUREMENT_SCALE = 1024 * 1024;
    protected static Map<String, Long> heapLimitations = new HashMap<>();
    protected static Map<String, Double> memoryLimitations = new HashMap<>();

    public static void reset() {
        fileSizeLimitations.clear();
        entryLimitations.clear();
        heapLimitations.clear();
        memoryLimitations.clear();
        FILE_ENTRY_LIMIT = -1;
        HEAP_LIMIT = -1;
        MEMORY_LIMIT_PERCENTAGE = -1;
        MEASUREMENT_SCALE = 1024 * 1024;
        EXCEPTION_ON_MEMORY_LIMIT = false;
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
     * Check if the occupied memory is reaching the set limit
     * 
     * @return true if the used memory has gone beyong the accepted threshhold,
     *         false
     *         otherwise
     */
    public static boolean hasReachedMemoryLimit() {
        return MEMORY_LIMIT_PERCENTAGE > -1 && MemoryUtils.getUsedMemoryPercentage() >= MEMORY_LIMIT_PERCENTAGE;
    }

    /**
     * Check if any memory limits have been reached
     * 
     * @return true if any memory limit has been reached, false otherwise
     * @throws MemoryLimitExceededException if EXCEPTION_ON_MEMORY_LIMIT is true and
     *                                      a limit is reached
     */
    public static boolean checkMemoryLimits(String location) {
        if (location == null) {
            location = String.valueOf(System.currentTimeMillis());
        }

        if (hasReachedHeapLimit()) {
            String message = "Heap limit of " + HEAP_LIMIT + " MB has been reached at " + location;
            log.warn(message);
            heapLimitations.put(location, Runtime.getRuntime().totalMemory());
            System.gc();

            if (EXCEPTION_ON_MEMORY_LIMIT) {
                throw new MemoryLimitExceededException(message);
            }
            return true;
        }
        if (hasReachedMemoryLimit()) {
            String message = "Memory usage limit of " + MEMORY_LIMIT_PERCENTAGE + "% has been reached at " + location;
            log.warn(message);
            memoryLimitations.put(location, MemoryUtils.getUsedMemoryPercentage());
            System.gc();

            if (EXCEPTION_ON_MEMORY_LIMIT) {
                throw new MemoryLimitExceededException(message);
            }
            return true;
        }
        return false;
    }

    public static boolean checkMemoryLimits() {
        return checkMemoryLimits(null);
    }

    /**
     * Manages the Guard Rails for the file parsing
     * 
     * @param in_fileName  the file to check
     * @param currentCount the current count of entries
     * @return true if a warning was generated
     */
    public static boolean checkEntryLimits(File in_fileName, int currentCount) {
        if (ParseGuardRails.hasReachedEntryLimit(currentCount)) {
            log.warn("Reached entry limit of {} for file {}. Skipping the remaining lines.",
                    ParseGuardRails.FILE_ENTRY_LIMIT, in_fileName);

            // Add file size info to map
            ParseGuardRails.entryLimitations.put(in_fileName.getAbsolutePath(), in_fileName.length());

            // Force garbage collection
            System.gc();
            return true;
        }

        return false;
    }

    public static void checkFileSizeLimits(File in_fileName) {
        // Large files should not cause a problem, but we should log it
        if (ParseGuardRails.hasReachedFileSizeLimit(in_fileName.length())) {
            log.warn("Reached file size limit of {} for file {}. Skipping the remaining lines.",
                    ParseGuardRails.FILE_SIZE_LIMIT, in_fileName);

            ParseGuardRails.fileSizeLimitations.put(in_fileName.getAbsolutePath(), in_fileName.length());
        }
    }

    private static boolean hasReachedFileSizeLimit(long length) {
        return FILE_SIZE_LIMIT > -1 && (length / MEASUREMENT_SCALE) >= FILE_SIZE_LIMIT;
    }

    /**
     * Returns a map containing all anomalies discovered during parsing
     * 
     * @return Map containing all limitation reports
     */
    public static Map<String, Set<String>> getAnomalyReport() {
        Map<String, Set<String>> report = new HashMap<>();

        report.put("heapLimitations", heapLimitations.keySet());
        report.put("memoryLimitations", memoryLimitations.keySet());
        report.put("fileSizeLimitations", fileSizeLimitations.keySet());
        report.put("entryLimitations", entryLimitations.keySet());

        return report;
    }

}