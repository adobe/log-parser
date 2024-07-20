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
import java.util.List;

public enum RunArguments {
    START_DIR("--startDir=", true, "The link to the Adobe Campaign site to crawl.",""),
    FILTER_LOG_FILES("--fileFilter=", false, "The wildcard needed for parsing the log files.","*.log"),

    PARSE_DEFINITIONS_FILE("--model=",true, "The model file needed for the analysis",""),
    TARGET_SDK_CLASS("--sdkClass=", false, "The SDK class to be used for transforming the log data to objects. It is just the class name.","GenericEntry"),
    REPORT_FORMAT("--reportType=",false, "This allows us to extract the results into a given format. Currently CSS & HTML are available.","HTML"),
    REPORT_FILENAME("--reportFileName=",false, "The extraction filename.",""),
    REPORT_NAME("--reportName=",false, "The report title.",""),


    HELP("--help", false, "Prints a help message","");

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

    public static String printCommands() {
        String lr_value = "";
        for (RunArguments lt_command : values()) {
            lr_value += "," + lt_command.toString();
        }
        return lr_value.substring(1);
    }

    public static boolean containsValue(String in_commandString) {

        for (RunArguments lt_command : RunArguments.values()) {
            if (lt_command.getLabel().equals(in_commandString))
                return true;
        }

        return false;
    }

    /**
     * This method returns all mandatory commands
     *
     * @return
     */
    public static List<RunArguments> getMandatoryCommands() {
        List<RunArguments> lr_manadatoryCommands = new ArrayList<>();

        for (RunArguments lt_command : RunArguments.values()) {
            if (lt_command.isMandatory())
                lr_manadatoryCommands.add(lt_command);
        }
        return lr_manadatoryCommands;
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
     * @param in_value
     * @return
     */
    public boolean command_equals(String in_value) {
        return in_value.startsWith(getLabel());

    }

    public static void printHelp() {
        System.out.println("NextUITestCrawler - Functional crawler for the Adobe Campaign Next");
        System.out.println();
        System.out.println("Execution line : ");
        System.out.println("java -jar NextUITestCrawler.jar --host=<Root URL for your AC Instance> --login=<Access Login> --password=<Access Password> ");
        System.out.println();

        // First Print the Mandatory fields
        System.out.println("Mandatory Arguments: ");
        for (RunArguments lt_command : RunArguments.values()) {
            if (lt_command.isMandatory()) {
                System.out.println(lt_command.fetchArgument() + "\t\t: "
                        + lt_command.getDescription());
            }
        }

        System.out.println();
        System.out
                .println("Optional Arguments (A default value will be selected):");
        for (RunArguments lt_command : RunArguments.values()) {
            if (!lt_command.isMandatory()) {
                System.out.println(lt_command.fetchArgument() + "\t\t: "
                        + lt_command.getDescription());
            }
        }

    }

    public String fetchArgument() {
        if (this.getLabel().endsWith("="))
            return this.getLabel().substring(0, this.getLabel().indexOf("="));
        else
            return this.getLabel();
    }

    public String fetchValue(String in_argument) {
        if (!this.command_equals(in_argument)) {
            throw new IllegalArgumentException("The given argument "
                    + in_argument
                    + " does not correspond to the argument command "
                    + this.fetchArgument());
        }

        String lr_String = in_argument.substring(this.getLabel().length());
        return lr_String.isEmpty() ? this.getDefaultValue() : lr_String;
    }

    public static boolean fillSystemWithArgs(List<String> in_args) {

        // Check that print help has not been pressed
        for (String lt_currentArg : in_args) {
            if (RunArguments.HELP.command_equals(lt_currentArg)) {
                RunArguments.printHelp();
                return false;
            }
        }

        // Check Mandatory
        for (RunArguments lt_mandatoryCommand : RunArguments.getMandatoryCommands()) {

            boolean lt_found = false;
            for (String lt_currentArg : in_args) {
                if (lt_mandatoryCommand.command_equals(lt_currentArg))
                    lt_found = true;
            }

            if (!lt_found) {
                System.out.println("Missing mandatory argument "
                        + lt_mandatoryCommand
                        + ". Please enter a value for that argument.\n"
                        + lt_mandatoryCommand.getDescription() + "\n\n");
                //RunAppHandler.printHelp();
                return false;
            }
        }

        // Fetch fields. for each missing field print warning and description
        // Browse Commands
        for (RunArguments lt_Command : RunArguments.values()) {
            for (String lt_currentArg : in_args) {
                if (lt_Command.command_equals(lt_currentArg)) {

                }
            }

            if (!lt_Command.equals(RunArguments.HELP))
                System.out.println("You did not give a value for the argument "+lt_Command.fetchArgument()+". The default value will be used. "+lt_Command.getDescription());
        }

        return true;
    }


}
