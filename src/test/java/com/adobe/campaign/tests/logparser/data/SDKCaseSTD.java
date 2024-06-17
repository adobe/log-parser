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

import java.util.*;
import java.util.stream.Collectors;

public class SDKCaseSTD extends StdLogEntry {
    String code;
    String errorMessage;

    public SDKCaseSTD() {
    }

    public SDKCaseSTD(SDKCaseSTD accstdErrors) {
        this.code = accstdErrors.code;
        this.errorMessage = accstdErrors.errorMessage;
    }

    @Override
    public String makeKey() {
        return code;
    }

    @Override
    public SDKCaseSTD copy() {
        return new SDKCaseSTD(this);
    }

    @Override
    public Set<String> fetchHeaders() {
        Set<String> lr_headerSet = new LinkedHashSet<>();
        lr_headerSet.addAll(Arrays.asList(StdLogEntry.STD_DATA_FILE_PATH, StdLogEntry.STD_DATA_FILE_NAME,"code", "errorMessage", "frequence"));
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

        return lr_map;
    }

    @Override
    public void setValuesFromMap(Map<String, String> in_valueMap) {
        super.setValuesFromMap(in_valueMap);

        var errorCode = in_valueMap.get("errorContent").split(" ")[0];
        this.code = errorCode.contains("-") ? errorCode : in_valueMap.get("errorContent");
        this.errorMessage = errorCode.contains("-") ? in_valueMap.get("errorContent")
                .substring(in_valueMap.get("errorContent").indexOf(" ") + 1) : in_valueMap.get("errorContent");
    }

    protected List<String> fetchValuesAsList() {

        return this.fetchHeaders().stream().map(e -> fetchValueMap().get(e).toString()).collect(Collectors.toList());
    }

}
