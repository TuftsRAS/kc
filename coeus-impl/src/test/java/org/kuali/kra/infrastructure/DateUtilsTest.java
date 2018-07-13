/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.infrastructure;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.sys.framework.util.DateUtils;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {
    private static final Logger LOG = LogManager.getLogger(DateUtilsTest.class);
    private static final ArrayList<String> TIME_LIST = new ArrayList<String>();
    
    private static String[][] valueChecks = { { "11:59 AM", "11:59 AM" }, { "01:59 AM", "1:59 AM" }, { "1:59 AM", "1:59 AM" },
                  { "1AM", "1:00 AM" }, { "3:00", "3:00 AM" }, { "3", "3:00 AM" }, { "12:00", "12:00 PM" }, { "17:00", "5:00 PM" },
                  { "1730", "5:30 PM" }, { "0800", "8:00 AM" }, { "5P", "5:00 PM" }, { "6:00a", "6:00 AM" }, { "6:00AM", "6:00 AM" },
                  { "8.30", "8:30 AM" }, { "2 p.m.", "2:00 PM" }, { "00:00", "12:00 AM" }, {null, ""}, {"23:36 pm", "11:36 PM"}, {"23:36 aM", "11:36 PM"},
                  { "   ", "" }, {"1200", "12:00 PM"}, {"1156", "11:56 AM"}, {"1300", "1:00 PM"}};
              
    
    @Before
    public void setUp() {
        TIME_LIST.add("11:59 AM");
        TIME_LIST.add("1:00 AM");
        TIME_LIST.add("12:00 PM");
    }
    
    @After
    public void tearDown() {
        TIME_LIST.clear();
        
    }
    @Test
    public void testConvertToObject() {
        Assert.assertTrue(TIME_LIST.contains(DateUtils.formatFrom12Or24Str("1A")));
        Assert.assertTrue(TIME_LIST.contains(DateUtils.formatFrom12Or24Str("11:59")));
        
    }
    
    @Test
    public void testConvertToObject2() {
        for (String[] values : valueChecks) {
            String canonicalized = DateUtils.formatFrom12Or24Str(values[0]);
            LOG.info("Original Value: '" + values[0] + "'  calculated value: '" + canonicalized + "'  expected value: '" + values[1] + "'");
            assertEquals(values[1], canonicalized);
        }
    }
    

}
