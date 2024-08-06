/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;



public class RunArgumentsTests {


    @Test(description="Testing the print help")
    public void testPrintHelp() {
        RunArguments.printHelp();
    }

    @Test
    public void testIsCorrectFormat() {
        assertThat("The argument should be of good format", RunArguments.isOfCorrectFormat("--startDir=Firefox"));

        assertThat("The argument should not be of good format", !RunArguments.isOfCorrectFormat("-startDir=Firefox"));
        assertThat("The argument should not be of good format", !RunArguments.isOfCorrectFormat("--startDir:Firefox"));
        assertThat("The argument should not be of good format", !RunArguments.isOfCorrectFormat("--="));

        assertThat("The argument should not be of good format", !RunArguments.isOfCorrectFormat("-"));
        assertThat("The argument should not be of good format", !RunArguments.isOfCorrectFormat(null));
    }

    @Test
    public void testCorrepondsTo() {
        assertThat("The argument should correspond", RunArguments.START_DIR.correspondsTo("--startDir=Firefox"));

        assertThat("The argument should Not correspond", !RunArguments.START_DIR.correspondsTo("--NotstartDir=Firefox"));
        assertThat("The argument should Not correspond", !RunArguments.START_DIR.correspondsTo("--startDirNot=Firefox"));

    }

    @Test
    public void testCorrepondsTo_negative() {
        assertThat("The argument should correspond", !RunArguments.START_DIR.correspondsTo("-"));

        assertThat("The argument should correspond", !RunArguments.START_DIR.correspondsTo(null));
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

        assertThat("We shoudl not have a value here",RunArguments.START_DIR.fetchValue(l_argument), Matchers.nullValue());

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

    @Test
    public void testBuildArgumentEntry() {
        assertThat("We should correctly build an entry for file filter", RunArguments.FILTER_LOG_FILES.buildArgument("kaboom"),
                Matchers.equalTo("--fileFilter=kaboom"));
    }

    @Test
    public void testFindValues() {
        String[] l_args = new String[]{RunArguments.FILTER_LOG_FILES.buildArgument("A")};

        assertThat("We should find the value", RunArguments.FILTER_LOG_FILES.fetchValue(l_args), Matchers.equalTo("A"));

        assertThat("We should find the value", RunArguments.REPORT_FORMAT.fetchValue(l_args, null), Matchers.nullValue());
        assertThat("We should find the value", RunArguments.REPORT_FORMAT.fetchValue(l_args), Matchers.equalTo(RunArguments.REPORT_FORMAT.getDefaultValue()));

    }
}
