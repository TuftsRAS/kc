<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html:html>
   <head>
<link rel="stylesheet" href="kew/css/kuali.css" type="text/css" />

</head>

<c:set var="negotiationAttributes" value="${DataDictionary.Negotiation.attributes}" />
<c:set var="negotiationActivityAttributes" value="${DataDictionary.NegotiationActivity.attributes}" />

	<table  cellpadding="2" cellspacing="0" excludedParams="*">
        <tr>
            <td>
           		<i><c:out value="${negotiationAttributes.negotiationId.label}: ${KualiForm.document.negotiationList[0].negotiationId}"/></i>
            </td>
            <td>
           		<i>&nbsp|&nbsp <c:out value="${negotiationAttributes.associatedDocumentId.shortLabel}: ${KualiForm.document.negotiationList[0].associatedDocumentId}"/></i>
            </td>
            <td>
           		<i>&nbsp|&nbsp <c:out value="${negotiationAttributes.negotiatorName.shortLabel}: ${KualiForm.document.negotiationList[0].negotiatorName}"/></i>
            </td>
            <td>
           		<i>&nbsp|&nbsp<c:out value="${negotiationAttributes.negotiationAgreementTypeId.label}: ${KualiForm.document.negotiationList[0].negotiationAgreementType.description}"/></i>
            </td>         

        </tr>
    </table>
   <div>&nbsp;</div> 


<display:table name="${KualiForm.negotiationActivityHistoryLineBeans}" id="row" class="datatable-100"   
	cellpadding="0" cellspacing="0" excludedParams="*">  
  <display:column property="location" title="${negotiationActivityAttributes.locationId.label}" class="infocell" />
  <display:column property="activityType" title="${negotiationActivityAttributes.activityTypeId.label}" class="infocell" />
  <display:column property="startDate" title="${negotiationActivityAttributes.startDate.label}" format="{0,date,MM/dd/yyyy}" class="infocell" />
  <display:column property="endDate" title="${negotiationActivityAttributes.endDate.label}" format="{0,date,MM/dd/yyyy}" class="infocell" />
  <display:column property="description" title="${negotiationActivityAttributes.description.label}" class="infocell" />
</display:table>


</html:html>
