/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.framework.krms;

import org.kuali.coeus.common.framework.sponsor.Sponsorable;
import org.kuali.coeus.common.framework.version.sequence.owner.SequenceOwner;

public interface KcKrmsJavaFunctionTermService {

    Boolean checkPropertyValueForAnyPreviousVersion(SequenceOwner<?> currentVersion, String property, String comparison);

    Boolean hasPropertyChangedThisVersion(SequenceOwner<?> currentVersion, String property);

    Boolean doSponsorAndPrimeSponsorMatch(Sponsorable grantsBo);
    
    Boolean doesPrimeSponsorTypeMatch(Sponsorable documentBo, String primeSponsorType);

}