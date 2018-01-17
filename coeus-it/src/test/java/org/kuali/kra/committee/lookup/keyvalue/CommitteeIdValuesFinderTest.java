/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.committee.lookup.keyvalue;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.coeus.common.committee.impl.bo.CommitteeBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.coeus.sys.framework.util.ValuesFinderUtils;
import org.kuali.kra.committee.bo.Committee;
import org.kuali.kra.test.infrastructure.KcIntegrationTestBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.ArrayList;
import java.util.List;

public class CommitteeIdValuesFinderTest extends KcIntegrationTestBase {

    
    private static final String CMT_1_ID = "c1";
    private static final String CMT_2_ID = "c2";
    private static final String CMT_3_ID = "c3";
    private static final String CMT_4_ID = "c4";
    
    private static final String C1_LATEST_NAME = "c1Latest";
    private static final String C2_LATEST_NAME = "c2Latest";
    private static final String C3_LATEST_NAME = "c3Latest";
    private static final String C4_LATEST_NAME = "c4Latest";
    
    /**
     * This method is to look for IRB specific committee.
     */
    @Test
    public void testGetActiveCommittees(){
        CommitteeIdValuesFinder finder = new CommitteeIdValuesFinder();
        finder.setBusinessObjectService(KcServiceLocator.getService(BusinessObjectService.class));
        List<CommitteeBase> results = finder.getActiveCommittees();
        Assert.assertEquals(1, results.size());
    }
    
    // NOTE: this method tests with an empty exclusion list of committees, 
    // it's not quite clear in what context(s) would this list be non-empty
    @Test
    public void testGetKeyValues(){
        Committee committee1 = new Committee();
        committee1.setCommitteeId(CMT_1_ID);
        committee1.setCommitteeName(C1_LATEST_NAME);
        
        Committee committee2 = new Committee();
        committee2.setCommitteeId(CMT_2_ID);
        committee2.setCommitteeName(C2_LATEST_NAME);
        
        Committee committee3 = new Committee();
        committee3.setCommitteeId(CMT_3_ID);
        committee3.setCommitteeName(C3_LATEST_NAME);
        
        Committee committee4 = new Committee();
        committee4.setCommitteeId(CMT_4_ID);
        committee4.setCommitteeName(C4_LATEST_NAME);
        
        final List<CommitteeBase> activeCommittees = new ArrayList<CommitteeBase>();
        activeCommittees.add(committee1);
        activeCommittees.add(committee2);
        activeCommittees.add(committee3);
        activeCommittees.add(committee4);
        
        KeyValue klp0 = ValuesFinderUtils.getSelectOption();
        KeyValue klp1 = new ConcreteKeyValue(CMT_1_ID, C1_LATEST_NAME);
        KeyValue klp2 = new ConcreteKeyValue(CMT_2_ID, C2_LATEST_NAME);
        KeyValue klp3 = new ConcreteKeyValue(CMT_3_ID, C3_LATEST_NAME);
        KeyValue klp4 = new ConcreteKeyValue(CMT_4_ID, C4_LATEST_NAME);
       
        // create an anonymous instance of the finder overriding the
        // getActiveCommittees() method, with our mock
        CommitteeIdValuesFinder finder = new CommitteeIdValuesFinder(){
            @Override
            public List<CommitteeBase> getActiveCommittees() {
                return activeCommittees;
            }
        };
        List<KeyValue> results = finder.getKeyValues();
        
        Assert.assertEquals(5, results.size());
        Assert.assertTrue(results.contains(klp0));
        Assert.assertTrue(results.contains(klp1));
        Assert.assertTrue(results.contains(klp2));
        Assert.assertTrue(results.contains(klp3));
        Assert.assertTrue(results.contains(klp4));
        
    }
    
}
