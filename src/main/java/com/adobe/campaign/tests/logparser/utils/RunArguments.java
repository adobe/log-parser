/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum RunArguments {
    START_DIR("startDir", true, "The root path from which the logs should be searched.", ""),
    FILTER_LOG_FILES("fileFilter", false, "The wildcard used for selecting the log files.", "*.log"),

    PARSE_DEFINITIONS_FILE("parseDefinition", true, "The model file needed for the analysis.", ""),
    TARGET_SDK_CLASS("sdkClass", false,
            "The SDK class to be used for transforming the log data to objects. It is just the class name.",
            "com.adobe.campaign.tests.logparser.core.GenericEntry"),
    REPORT_FORMAT("reportType", false,
            "The format of the report. The allowed values are currently HTML & CSV.", "HTML"),
    REPORT_FILENAME("reportFileName", false, "The name of the report file. By default, this is the name of the Parse Definition name suffixed with '-export'", ""),
    REPORT_NAME("reportName", false, "The report title as show in an HTML report. By default the title includes the Parse Definition name", ""),

    HELP("help", false, "Prints a help message", ""){

    };

    private final String label;
    private final boolean mandatory;
    private final String description;
    private final String defaultValue;

    /**
     * @param label
     * @param mandatory
     * @param description
     */
    private RunArguments(String label, boolean mandatory,
            String description, String defaultValue) {
        this.label = label;
        this.mandatory = mandatory;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    /**
     * This method returns all mandatory commands
     *
     * @return
     */
    public static List<RunArguments> getMandatoryCommands() {
        List<RunArguments> lr_manadatoryCommands = new ArrayList<>();

        for (RunArguments lt_command : RunArguments.values()) {
            if (lt_command.isMandatory()) {
                lr_manadatoryCommands.add(lt_command);
            }
        }
        return lr_manadatoryCommands;
    }

    /**
     * This method checks if the argument is of a correct format
     *
     * @param in_argument the argument to analyze
     * @return true if the format of the argument is of the form --label=value
     */
    public static boolean isOfCorrectFormat(String in_argument) {
        if (in_argument == null || in_argument.length() < 3) {
            return false;
        }

        if (!in_argument.startsWith(getCommandPrefix()) || !in_argument.contains(getKeyValueSeparator())) {
            return false;
        }

        if (in_argument.substring(getCommandPrefix().length()).split(getKeyValueSeparator()).length < 2) {
            return false;
        }

        return true;
    }

    public static void printHelp() {
        System.out.println("LogParser - Parse Log Files and generate reports");
        System.out.println();
        System.out.println("Execution line : ");
        System.out.println(
                "java -jar LogParser.jar --parseDefinition=<The file path of the parse definitions> --startDir=<Root Path where the logs are stored> ");
        System.out.println();

        // First Print the Mandatory fields
        System.out.println("Mandatory Arguments: ");
        for (RunArguments lt_command : RunArguments.values()) {
            if (lt_command.isMandatory()) {
                System.out.println(RunArguments.getCommandPrefix()+lt_command.getLabel() + "\t\t: "
                        + lt_command.getDescription());
            }
        }

        System.out.println();
        System.out
                .println("Optional Arguments (A default value will be selected):");
        for (RunArguments lt_command : RunArguments.values()) {
            if (!lt_command.isMandatory()) {
                System.out.println(RunArguments.getCommandPrefix()+lt_command.getLabel() + "\t\t: "
                        + lt_command.getDescription() + ((lt_command.getDefaultValue().length() > 0) ?
                        " By default, the value is " + lt_command.getDefaultValue() : ""));
            }
        }

    }

    private static String getCommandPrefix() {
        return "--";
    }

    private static String getKeyValueSeparator() {
        return "=";
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the mandatory
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Checks if the given string equals the value of the command
     *
     * @param in_argumentEntry
     * @return true if the command corresponds to the given string
     */
    public boolean correspondsTo(String in_argumentEntry) {
        if (in_argumentEntry == null || in_argumentEntry.length() < getCommandPrefix().length()) {
            return false;
        }
        return in_argumentEntry.split(getKeyValueSeparator())[0].equals(getCommandPrefix() + getLabel());

    }

    /**
     * This method fetches the value of the argument
     *
     * @param in_argument the argument name
     * @return the value of the argument.
     */
    public String fetchValue(String in_argument) {
        if (!this.correspondsTo(in_argument)) {
            return null;
        }

        String lr_String = in_argument.substring(
                this.getLabel().length() + getCommandPrefix().length() + getKeyValueSeparator().length());
        return lr_String.isEmpty() ? this.getDefaultValue() : lr_String;
    }

    /**
     * This method builds the argument as it is passed to the main method
     *
     * @param argumentValue
     * @return
     */
    public String buildArgument(String argumentValue) {
        return buildTag() + getKeyValueSeparator() + argumentValue;
    }

    /**
     * This method finds the value from an array of arguments
     *
     * @param in_args an array of arguments
     * @return The value for the argument, null if it is not found
     */
    public String fetchValue(String[] in_args) {
        return fetchValue(in_args, this.getDefaultValue());
    }

    /**
     * This method finds the value from an array of arguments
     *
     * @param in_args an array of arguments
     * @param in_defaultValue The default value to return if the argument is not found
     * @return The value for the argument, null if it is not found
     */
    public String fetchValue(String[] in_args, String in_defaultValue) {
        return Arrays.stream(in_args).map(a -> this.fetchValue(a)).filter(a -> a != null).findFirst().orElse(in_defaultValue);
    }

    public String buildTag() {
        return getCommandPrefix() + this.getLabel();
    }
}
