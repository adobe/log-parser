package com.adobe.campaign.tests.logparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.Test;

public class StdLogEntryTests {

    @Test
    public void testSimplePut()  {
        
        GenericEntry l_inputData = new GenericEntry();
        l_inputData.fetchValueMap().put("AAZ", "12");
        
        GenericEntry l_inputData2 = new GenericEntry();
        l_inputData2.put("AAZ", "12");

        assertThat(l_inputData, is(equalTo(l_inputData2)));
    }
    
    @Test
    public void testSimpleGet()  {
        
        
        GenericEntry l_inputData2 = new GenericEntry();
        l_inputData2.put("AAZ", "12");

        assertThat(l_inputData2.get("AAZ"), is(equalTo("12")));
    }
}
