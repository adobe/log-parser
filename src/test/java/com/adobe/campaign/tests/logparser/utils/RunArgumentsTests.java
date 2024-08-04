/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;


public class RunArgumentsTests {

    @Test(description="Testing that we can correctly fetch the label without the suffix '='")
    public void testRealLabel() {
        String l_result = RunArguments.START_DIR.fetchArgument();

        Assert.assertEquals(l_result, "--startDir");
    }


    @Test(description="Testing the print help")
    public void testPrintHelp() {
        RunArguments.printHelp();
    }

    @Test(description="Testing the possibility of retreiving the argument value from the given argument data.")
    public void testArgValue() {
        String l_argument="--startDir=Firefox";

        Assert.assertEquals(RunArguments.START_DIR.fetchValue(l_argument), "Firefox");

        Assert.assertNotEquals(RunArguments.START_DIR.fetchValue(l_argument), "Firefoxdsd");
    }

    @Test(description="Testing the possibility of retreiving the argument value from the given argument data.")
    public void testArgValue_negativeNonExistant() {
        String l_argument="--startDirNonExistant=Firefox";

        Assert.assertThrows(IllegalArgumentException.class, () -> RunArguments.START_DIR.fetchValue(l_argument));

    }

    @Test(description="Testing the possibility of retreiving the argument value from an empty argument data.")
    public void testArgValueEmpty() {
        String l_argument="--fileFilter=";

        Assert.assertEquals(RunArguments.FILTER_LOG_FILES.fetchValue(l_argument), "*.log");
    }

    @Test
    public void testGetMandatoryCommands() {
        var mandatoryCommands = RunArguments.getMandatoryCommands();
        assertThat("We should have the mandatory commands", mandatoryCommands.size() > 0);
        assertThat("We should at least have the parse definition file", mandatoryCommands.contains(RunArguments.PARSE_DEFINITIONS_FILE));
    }
}
