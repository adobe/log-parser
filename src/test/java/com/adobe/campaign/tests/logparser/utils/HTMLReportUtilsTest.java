/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;


public class HTMLReportUtilsTest {
    @Test
    public void generateHeadersTest() {
        Collection<String> headers = List.of("header1", "header2", "header3");

        String result = HTMLReportUtils.generateHeaders(headers);

        assertThat("We should have the <thead> in the result", result.contains("<thead>"));
        assertThat("We should have the headers in the result", result.contains("header1"));
        assertThat("We should have the headers in the result", result.contains("header2"));
        assertThat("We should have the headers in the result", result.contains("header3"));
        assertThat("We should have the </thead> in the result", result.contains("</thead>"));
        assertThat("We should have the <th> in the result", result.contains("<th>"));
        assertThat("We should not have the <td> in the result", !result.contains("<td>"));



    }
}
