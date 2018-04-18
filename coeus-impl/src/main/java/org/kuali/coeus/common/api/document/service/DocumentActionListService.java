/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document.service;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;

import java.util.Collection;
import java.util.List;

public interface DocumentActionListService {
    List<ActionRequestValue> populateRouteLogFormActionRequests(DocumentRouteHeaderValue routeHeader);

    List<ActionRequestValue> switchActionRequestPositionsIfPrimaryDelegatesPresent(Collection<ActionRequestValue> actionRequests);

    ActionRequestValue switchActionRequestPositionIfPrimaryDelegatePresent(ActionRequestValue actionRequest);

    void fixActionRequestsPositions(DocumentRouteHeaderValue routeHeader);

    List<ActionRequestValue> populateRouteLogFutureRequests(DocumentRouteHeaderValue document) throws Exception;
}
