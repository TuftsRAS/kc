/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.keyvalue;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class extends the PersistableBusinessObjectValuesFinder - the Generic ValuesFinder - 
 * to add a "select" entry.
 * 
 * <p>
 * It's a shame that rice is so tightly coupled to {@link PersistableBusinessObjectValuesFinder PersistableBusinessObjectValuesFinder}
 * It makes the design of these value finders very rigid.
 * </p>
 */
public class ExtendedPersistableBusinessObjectValuesFinder extends PersistableBusinessObjectValuesFinder {

    private static final Comparator<KeyValue> PBO_COMPARATOR = new PBOComparator();
    private static final ConcreteKeyValue SELECT_PREFIX = new ConcreteKeyValue(PrefixValuesFinder.getPrefixKey(), PrefixValuesFinder.getDefaultPrefixValue());
    private static final Logger LOG = LogManager.getLogger(ExtendedPersistableBusinessObjectValuesFinder.class);

    private static class PBOComparator implements Comparator<KeyValue>
    {    
        @Override
        public int compare(KeyValue kv1, KeyValue kv2 )
        {    
            try {
                String desc1 = kv1.getValue();
                String desc2 = kv2.getValue();
                if (desc1 == null) {
                    desc1 = "";
                }
                if (desc2 == null) {
                    desc2 = "";
                }
                return desc1.compareTo(desc2);  
            } catch (RuntimeException e) {
                LOG.error("Exception sorting KeyValues", e);
                return 0;
            }
        }
        
    }
    /**
     * Build the list of KeyValues using the key (keyAttributeName) and
     * label (labelAttributeName) of the list of all business objects found
     * for the BO class specified along with a "select" entry.
     */
    @Override
    public List<KeyValue> getKeyValues(){
        final List<KeyValue> labels = super.getKeyValues();
        labels.sort(PBO_COMPARATOR);
        labels.add(0, SELECT_PREFIX);
        return labels;
    }
}
