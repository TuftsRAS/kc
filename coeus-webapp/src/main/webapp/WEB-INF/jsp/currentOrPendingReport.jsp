<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>
<kul:page  docTitle="Current & Pending Support"  transactionalDocument="false" 
htmlFormAction="currentOrPendingReport">
<div class="tab-container" align="center">
    	<h3>
    		<span>Print</span>
        </h3>
       
	    <kra:printReports requestUri="/currentOrPendingReport.do"/>
    </div> 
</kul:page>

