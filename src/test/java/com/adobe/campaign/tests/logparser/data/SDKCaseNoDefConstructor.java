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

public class SDKCaseNoDefConstructor extends StdLogEntry {
    String code;
    String errorMessage;

    @Override
    public Set<String> fetchStoredHeaders() {
        return super.fetchStoredHeaders();
    }


    public SDKCaseNoDefConstructor(SDKCaseNoDefConstructor accstdErrors) {
        this.code = accstdErrors.code;
        this.errorMessage = accstdErrors.errorMessage;
    }

    @Override
    public String makeKey() {
        return code;
    }

    @Override
    public SDKCaseNoDefConstructor copy() {
        return new SDKCaseNoDefConstructor(this);
    }

    @Override
    public Set<String> fetchHeaders() {
        Set<String> lr_headerSet = new LinkedHashSet<>();
        lr_headerSet.addAll(Arrays.asList("fileName","code", "errorMessage", "frequence"));
        return lr_headerSet;
    }

    @Override
    public Map<String, Object> fetchValueMap() {
        Map<String, Object> lr_map = new HashMap<>();
        lr_map.put("fileName", this.getFileName());
        lr_map.put("code", this.makeKey());
        lr_map.put("errorMessage", this.errorMessage);
        lr_map.put("frequence", this.getFrequence());

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

    @Override
    protected List<String> fetchValuesAsList() {

        return this.fetchHeaders().stream().map(e -> fetchValueMap().get(e).toString()).collect(Collectors.toList());
    }
}
