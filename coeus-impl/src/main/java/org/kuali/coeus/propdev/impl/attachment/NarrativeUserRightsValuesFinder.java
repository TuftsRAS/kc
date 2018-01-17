/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.attachment;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * This class used to populate radio buttons for narrative user rights
 * @author KRADEV team
 * @version 1.0
 */
public class NarrativeUserRightsValuesFinder extends UifKeyValuesFinderBase {
    /**
     * Returns Narrative user right values 
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("R","Read"));
        keyValues.add(new ConcreteKeyValue("M","Modify"));
        keyValues.add(new ConcreteKeyValue("N","None"));
        return keyValues;
    }
}
