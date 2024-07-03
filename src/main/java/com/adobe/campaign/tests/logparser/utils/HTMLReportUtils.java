/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import java.util.Collection;

public class HTMLReportUtils {

    /**
     * Given a collection of headers, we will generate the HTML headers
     * @param headers a collection of headers
     * @return a string containing the headers in HTML format
     */
    public static String generateHeaders(Collection<String> headers) {
        StringBuilder lr_builder = new StringBuilder();
        lr_builder.append("<thead>");
        lr_builder.append("<tr>");
        headers.stream().forEach(j -> lr_builder.append("<th>").append(j).append("</th>"));
        lr_builder.append("</tr>");
        lr_builder.append("</thead>");
        return lr_builder.toString();
    }
}
