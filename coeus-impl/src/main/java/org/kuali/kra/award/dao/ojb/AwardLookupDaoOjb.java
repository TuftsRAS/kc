/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.dao.ojb;

import static org.kuali.kra.award.home.AwardConstants.AWARD_SEQUENCE_STATUS;

import org.kuali.kra.award.dao.AwardLookupDao;
import org.kuali.kra.award.home.Award;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.lookup.CollectionIncomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class AwardLookupDaoOjb extends LookupDaoOjb  implements AwardLookupDao{

    private static final String ACTIVE_OR_PENDING_AWARD_SEQUENCE_STATUS = "ACTIVE|PENDING";
    private static final String BOTH_AWARD_SEQUENCE_STATUS = "BOTH";

    @SuppressWarnings("rawtypes")
    @Override
    public List<? extends BusinessObject> getAwardSearchResults(Map fieldValues, boolean usePrimaryKeys) {
        if (fieldValues.containsKey(AWARD_SEQUENCE_STATUS) &&
                fieldValues.get(AWARD_SEQUENCE_STATUS).toString().equalsIgnoreCase(BOTH_AWARD_SEQUENCE_STATUS)) {
            fieldValues.remove(AWARD_SEQUENCE_STATUS);
            fieldValues.put(AWARD_SEQUENCE_STATUS, ACTIVE_OR_PENDING_AWARD_SEQUENCE_STATUS);
        }
        
        Collection<Award> searchResults = findAwards(fieldValues, usePrimaryKeys);
        Collection<Award> newest = searchResults.stream()
        	.sorted(Comparator.comparing(Award::getAwardNumber).thenComparing(Comparator.comparing(Award::getSequenceNumber).reversed()))
        	.collect(Collectors.toMap(Award::getAwardNumber, Function.identity(), (p, q) -> p)).values();
        
        if (searchResults instanceof CollectionIncomplete) {
        	return new CollectionIncomplete<>(newest, ((CollectionIncomplete) searchResults).getActualSizeIfTruncated());
        } else {
        	return new ArrayList<>(newest);
        }
    }

	protected Collection<Award> findAwards(Map fieldValues, boolean usePrimaryKeys) {
		return this.findCollectionBySearchHelper(Award.class, fieldValues, false, usePrimaryKeys);
	}
}
