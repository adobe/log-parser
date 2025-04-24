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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ParseGuardRailsTest {

    @BeforeMethod
    @AfterMethod
    public void reset() {
        ParseGuardRails.reset();
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
                ParseGuardRails.hasReachedMemoryPercentageLimit(), is(false));
    }

    @Test
    public void testHasReachedMemoryPercentageLimit_WhenBelowLimit() {
        ParseGuardRails.MEMORY_LIMIT_PERCENTAGE = 20.0; // Set limit to 20%
        assertThat("Should not reach limit when below threshold",
                ParseGuardRails.hasReachedMemoryPercentageLimit(), is(false));
    }

    @Test
    public void testHasReachedMemoryPercentageLimit_WhenAtLimit() {

        ParseGuardRails.MEMORY_LIMIT_PERCENTAGE = 100.0; // Set limit to 0%
        assertThat("Should reach limit when at threshold",
                ParseGuardRails.hasReachedMemoryPercentageLimit(), is(true));
    }
}