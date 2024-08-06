/*
 * Copyright 2022 Adobe
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in
 * accordance with the terms of the Adobe license agreement accompanying
 * it.
 */
package com.adobe.campaign.tests.logparser.data;

import com.adobe.campaign.tests.logparser.core.StdLogEntry;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SDKCase2 extends StdLogEntry {
    String code;
    String errorMessage;
    ZonedDateTime timeOfLog;

    public SDKCase2() {
    }

    public SDKCase2(SDKCase2 accstdErrors) {
        this.code = accstdErrors.code;
        this.errorMessage = accstdErrors.errorMessage;
    }

    @Override
    public String makeKey() {
        return code;
    }

    @Override
    public SDKCase2 copy() {
        return new SDKCase2(this);
    }

    @Override
    public Set<String> fetchHeaders() {
        Set<String> lr_headerSet = new LinkedHashSet<>();
        lr_headerSet.addAll(Arrays.asList(StdLogEntry.STD_DATA_FILE_PATH, StdLogEntry.STD_DATA_FILE_NAME,"code", "errorMessage","timeOfLog", "frequence"));
        return lr_headerSet;
    }

    @Override
    public Map<String, Object> fetchValueMap() {
        Map<String, Object> lr_map = new HashMap<>();
        lr_map.put(StdLogEntry.STD_DATA_FILE_PATH, this.getFilePath());
        lr_map.put(StdLogEntry.STD_DATA_FILE_NAME, this.getFileName());
        lr_map.put("code", this.makeKey());
        lr_map.put("errorMessage", this.errorMessage);
        lr_map.put(StdLogEntry.STD_DATA_FREQUENCE, this.getFrequence());
        lr_map.put("timeOfLog", this.timeOfLog);

        getValuesMap().forEach(lr_map::putIfAbsent);

        return lr_map;
    }

    @Override
    public void setValuesFromMap(Map<String, String> in_valueMap) {
        super.setValuesFromMap(in_valueMap);

        var errorCode = in_valueMap.get("errorContent").split(" ")[0];
        this.code = errorCode.contains("-") ? errorCode : in_valueMap.get("errorContent");
        this.errorMessage = errorCode.contains("-") ? in_valueMap.get("errorContent")
                .substring(in_valueMap.get("errorContent").indexOf(" ") + 1) : in_valueMap.get("errorContent");

        this.timeOfLog = ZonedDateTime.parse(in_valueMap.get("timeStamp"), DateTimeFormatter.ISO_ZONED_DATE_TIME);

    }

    protected List<String> fetchValuesAsList() {

        return this.fetchHeaders().stream().map(e -> fetchValueMap().get(e).toString()).collect(Collectors.toList());
    }

}
