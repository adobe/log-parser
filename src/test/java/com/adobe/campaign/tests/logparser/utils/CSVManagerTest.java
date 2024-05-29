/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import com.github.javafaker.Faker;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class CSVManagerTest {

    Faker faker = new Faker();




    @Test
    public void fetchCoverageHistoryData_noFile() throws IOException {

        CSVManager csvManager = new CSVManager();

        final File l_csvFile = new File(faker.file().fileName());
        Map<String, List<String>> l_coverageHistory = csvManager
                .fetchCSVToMapList(faker.name().firstName(), l_csvFile);

        assertThat("We should have an empty map", l_coverageHistory.isEmpty());

    }



}
