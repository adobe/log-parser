/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.adobe.campaign.tests.logparser.exceptions.MemoryLimitExceededException;

public class ParseGuardRailsTest {

    private Path tempFile;

    @BeforeMethod
    public void setup() throws IOException {
        ParseGuardRails.reset();

        tempFile = Files.createTempFile("test", ".log");
    }

    @AfterMethod
    public void cleanup() throws IOException {
        ParseGuardRails.reset();
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testCheckFileSizeLimits_WhenFileSizeNotSet() {
        ParseGuardRails.checkFileSizeLimits(tempFile.toFile());
        assertThat("Should not have file size limitations when not set",
                ParseGuardRails.fileSizeLimitations.isEmpty(), is(true));
    }

    @Test
    public void testCheckFileSizeLimits_WhenFileSizeBelowLimit() throws IOException {
        ParseGuardRails.MEASUREMENT_SCALE = 1;
        ParseGuardRails.FILE_SIZE_LIMIT = 1000; // Set a high limit
        Files.write(tempFile, "small content".getBytes());
        ParseGuardRails.checkFileSizeLimits(tempFile.toFile());
        assertThat("Should not have recorded file size",
                ParseGuardRails.fileSizeLimitations.isEmpty());
    }

    @Test
    public void testCheckFileSizeLimits_WhenFileSizeAtLimit() throws IOException {
        ParseGuardRails.MEASUREMENT_SCALE = 1;
        Files.write(tempFile, "content".getBytes());
        ParseGuardRails.FILE_SIZE_LIMIT = tempFile.toFile().length(); // Set limit to 0
        ParseGuardRails.checkFileSizeLimits(tempFile.toFile());
        assertThat("Should have recorded file size",
                ParseGuardRails.fileSizeLimitations.containsKey(tempFile.toString()), is(true));
    }

    @Test
    public void testCheckFileSizeLimits_WhenFileSizeAboveLimit() throws IOException {
        ParseGuardRails.MEASUREMENT_SCALE = 1;

        ParseGuardRails.FILE_SIZE_LIMIT = 1; // Set a small limit
        Files.write(tempFile, "large content that exceeds the limit".getBytes());
        ParseGuardRails.checkFileSizeLimits(tempFile.toFile());
        assertThat("Should have recorded file size",
                ParseGuardRails.fileSizeLimitations.containsKey(tempFile.toString()), is(true));
    }

    @Test
    public void testHasReachedHeapLimit_WhenLimitNotSet() {
        assertThat("Should not reach limit when not set",
                ParseGuardRails.hasReachedHeapLimit(), is(false));
    }

    @Test
    public void testHasReachedHeapLimit_WhenBelowLimit() {
        ParseGuardRails.HEAP_LIMIT = 1000; // Set a high limit
        assertThat("Should not reach limit when below threshold",
                ParseGuardRails.hasReachedHeapLimit(), is(false));
    }

    @Test
    public void testHasReachedHeapLimit_WhenAboveLimit() {
        ParseGuardRails.HEAP_LIMIT = 1; // Set limit to 1 to force exceeding it
        ParseGuardRails.HEAP_SIZE_AT_START = -20; // Set heap size to -20 to force exceeding it
        assertThat("Should reach limit when above threshold",
                ParseGuardRails.hasReachedHeapLimit(), is(true));
    }

    @Test
    public void testHasReachedMemoryPercentageLimit_WhenLimitNotSet() {
        assertThat("Should not reach limit when not set",
                ParseGuardRails.hasReachedMemoryLimit(), is(false));
    }

    @Test
    public void testHasReachedMemoryPercentageLimit_WhenBelowLimit() {
        ParseGuardRails.MEMORY_LIMIT_PERCENTAGE = 20.0; // Set limit to 20%
        assertThat("Should not reach limit when below threshold",
                ParseGuardRails.hasReachedMemoryLimit(), is(false));
    }

    @Test
    public void testHasReachedMemoryPercentageLimit_WhenAtLimit() {

        ParseGuardRails.MEMORY_LIMIT_PERCENTAGE = 0.1; // Set limit to 0%
        assertThat("Should reach limit when at threshold",
                ParseGuardRails.hasReachedMemoryLimit(), is(true));
    }

    @Test
    public void testCheckMemoryLimits_WhenNoLimitsSet() {
        assertThat("Should not reach limit when not set",
                ParseGuardRails.checkMemoryLimits(), is(false));
    }

    @Test
    public void testCheckMemoryLimits_WhenHeapLimitReached() {
        ParseGuardRails.HEAP_LIMIT = 0; // Set limit to 0 to force reaching it
        assertThat("Should reach limit when heap limit is reached",
                ParseGuardRails.checkMemoryLimits(), is(true));
    }

    @Test
    public void testCheckMemoryLimits_WhenMemoryLimitReached() {
        ParseGuardRails.MEMORY_LIMIT_PERCENTAGE = 0.0; // Set limit to 0% to force reaching it
        assertThat("Should reach limit when memory limit is reached",
                ParseGuardRails.checkMemoryLimits(), is(true));
    }

    @Test
    public void testCheckMemoryLimits_WhenHeapLimitReachedWithException() {
        ParseGuardRails.EXCEPTION_ON_MEMORY_LIMIT = true;

        ParseGuardRails.HEAP_LIMIT = 1; // Set limit to 1 to force exceeding it
        ParseGuardRails.HEAP_SIZE_AT_START = -20; // Set heap size to -20 to force exceeding it

        assertThat("We should have reached the heap limit", ParseGuardRails.hasReachedHeapLimit());

        assertThrows(MemoryLimitExceededException.class, () -> ParseGuardRails.checkMemoryLimits());
    }

    @Test
    public void testCheckMemoryLimits_WhenMemoryLimitReachedWithException() {
        ParseGuardRails.EXCEPTION_ON_MEMORY_LIMIT = true;

        ParseGuardRails.MEMORY_LIMIT_PERCENTAGE = 0.0; // Set limit to 0% to force reaching it
        assertThrows(MemoryLimitExceededException.class, () -> ParseGuardRails.checkMemoryLimits());
    }
}