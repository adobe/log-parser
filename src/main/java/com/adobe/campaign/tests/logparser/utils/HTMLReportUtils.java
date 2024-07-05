/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class HTMLReportUtils {

    public static final String ROW_END = "</tr>";
    public static final String ROW_START = "<tr>";

    /**
     * Given a collection of headers, we will generate the HTML headers
     * @param headers a collection of headers
     * @return a string containing the headers in HTML format
     */
    public static String fetchTableHeaders(Collection<String> headers) {
        StringBuilder lr_builder = new StringBuilder();
        lr_builder.append("<thead>");
        lr_builder.append(ROW_START);
        headers.stream().forEach(j -> lr_builder.append("<th>").append(j).append("</th>"));
        lr_builder.append(ROW_END);
        lr_builder.append("</thead>");
        return lr_builder.toString();
    }

    /**
     * Fetches the CSS Style to be used in our reports
     * @param in_path the path to the style file
     * @return a string containing the CSS style in HTML format
     * @throws IOException if the file is not found
     */
    public static String fetchStyleInlineCSS(String in_path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<style>");
        sb.append(new String(Files.readAllBytes(Paths.get(in_path))));
        sb.append("</style>");
        return sb.toString();
    }

    /**
     * Returns a standard table to be used with the CSS style
     * @return A string containing the cell in HTML format
     */
    public static String fetchTableStartBracket() {
        return "<table class='diffOverView'>";
    }

    /**
     * Returns a cell given a content
     * @param in_content A content object
     * @return A string containing the cell in HTML format
     */
    public static String fetchCell_TD(Object in_content) {
        StringBuilder sb = new StringBuilder();
        return sb.append("<td>").append(in_content).append("</td>").toString();
    }

    /**
     * Attaches a header to the StringBuilder
     *
     * @param in_headerLevel the strength of the header
     * @param in_reportTitle The title of the header
     * @return a string containing the header in HTML format
     */
    public static String fetchHeader(int in_headerLevel, String in_reportTitle) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h").append(in_headerLevel).append(">");
        sb.append(in_reportTitle);
        sb.append("</h").append(in_headerLevel).append(">");
        return sb.toString();
    }

    /**
     * Creates the beginning of a standard HTML page
     * @return a string containing the beginning of a standard HTML page
     * @param in_path the path to the css file
     */
    public static String fetchSTDPageStart(String in_path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append(fetchStyleInlineCSS(in_path));
        sb.append("<body>");
        return sb.toString();
    }
}
