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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.adobe.campaign.tests.logparser.exceptions.MemoryLimitExceededException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseGuardRailsTest {

    private Path tempFile;
    private static final String ANOMALY_REPORT_PATH = "anomalies.json";

    @BeforeMethod
    public void setup() throws IOException {
        ParseGuardRails.reset();
        System.clearProperty("EXCEPTION_ON_MEMORY_LIMIT");
        tempFile = Files.createTempFile("test", ".log");
        // Clean up any existing anomaly report
        new File(ANOMALY_REPORT_PATH).delete();
    }

    @AfterMethod
    public void cleanup() throws IOException {
        ParseGuardRails.reset();
        System.clearProperty("EXCEPTION_ON_MEMORY_LIMIT");
        Files.deleteIfExists(tempFile);
        // Clean up the anomaly report
        new File(ANOMALY_REPORT_PATH).delete();
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

        ParseGuardRails.MEMORY_LIMIT_PERCENTAGE = 0.0; // Set limit to 0%
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

        ParseGuardRails.HEAP_SIZE_AT_START = -20; // Set heap size to -20 to force exceeding it

        ParseGuardRails.HEAP_LIMIT = 0; // Set limit to 0 to force reaching it
        assertThat("Should reach limit when heap limit is reached",
                ParseGuardRails.checkMemoryLimits(), is(true));

        assertThat("Should have anomaly report", ParseGuardRails.getAnomalyReport().size(), is(1));
        assertThat("Should have heap limitation", ParseGuardRails.getAnomalyReport().get("heapLimitations").size(),
                is(1));

        assertThat("Should not have a memory limitation",
                !ParseGuardRails.getAnomalyReport().containsKey("memoryLimitations"));

        assertThat("Should have file size limitation",
                !ParseGuardRails.getAnomalyReport().containsKey("fileSizeLimitations"));

        assertThat("Should have entry limitation",
                !ParseGuardRails.getAnomalyReport().containsKey("entryLimitations"));
    }

    @Test
    public void testCheckMemoryLimits_WhenMemoryLimitReached() {
        ParseGuardRails.MEMORY_LIMIT_PERCENTAGE = 0.0; // Set limit to 0% to force reaching it
        assertThat("Should reach limit when memory limit is reached",
                ParseGuardRails.checkMemoryLimits(), is(true));

        assertThat("Should have memory limitations", ParseGuardRails.memoryLimitations.size(), is(1));
        assertThat("Should no have heap limitations", ParseGuardRails.heapLimitations.isEmpty());
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

    @Test
    public void testExportAnomalyReport_WhenNoAnomalies() {
        ParseGuardRails.exportAnomalyReport();
        assertThat("Should not create file when no anomalies",
                (new File(ANOMALY_REPORT_PATH)).exists(), is(false));
    }

    @Test
    public void testExportAnomalyReport_WhenHasAnomalies() throws IOException {
        // Create some anomalies
        ParseGuardRails.HEAP_LIMIT = 1;
        ParseGuardRails.HEAP_SIZE_AT_START = -20;
        ParseGuardRails.checkMemoryLimits();

        ParseGuardRails.exportAnomalyReport();

        // Verify file exists
        File reportFile = new File(ANOMALY_REPORT_PATH);
        assertThat("Should create file when anomalies exist",
                reportFile.exists(), is(true));

        // Verify content
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<String>> report = mapper.readValue(reportFile, Map.class);
        assertThat("Should have heap limitations",
                report.containsKey("heapLimitations"));
        assertThat("Should have at least one heap limitation",
                report.get("heapLimitations").size(), Matchers.greaterThan(0));
    }

    @Test
    public void testExportAnomalyReport_WhenFileExists() throws IOException {
        // Create initial file
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Set<String>> initialData = Map.of("test", Set.of("data"));
        mapper.writeValue(new File(ANOMALY_REPORT_PATH), initialData);

        // Create some anomalies
        ParseGuardRails.HEAP_LIMIT = 1;
        ParseGuardRails.HEAP_SIZE_AT_START = -20;
        ParseGuardRails.checkMemoryLimits();

        ParseGuardRails.exportAnomalyReport();

        // Verify file was replaced
        Map<String, Set<String>> report = mapper.readValue(new File(ANOMALY_REPORT_PATH), Map.class);
        assertThat("Should have replaced old content",
                report.containsKey("heapLimitations"), is(true));
        assertThat("Should not have old content",
                report.containsKey("test"), is(false));
    }

    @Test
    public void testExportAnomalyReport_WhenIOExceptionOccurs() throws IOException {
        // Create some anomalies
        ParseGuardRails.HEAP_LIMIT = 1;
        ParseGuardRails.HEAP_SIZE_AT_START = -20;
        ParseGuardRails.checkMemoryLimits();

        // Create a file that will cause an IOException when trying to write
        File reportFile = new File(ANOMALY_REPORT_PATH);
        reportFile.createNewFile();
        reportFile.setReadOnly();

        // This should log an error but not throw an exception
        ParseGuardRails.exportAnomalyReport();

        // Clean up
        reportFile.setWritable(true);
        reportFile.delete();
    }
}