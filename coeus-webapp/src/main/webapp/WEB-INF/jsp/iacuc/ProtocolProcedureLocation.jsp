<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<script type="text/javascript">
   var $j = jQuery.noConflict();
   $j(document).ready(function() {
	   populateSelect('getIacucProcedureLocationNames', 'locationTypeCode', 'locationId');
   });
</script>


<kul:documentPage
	showDocumentInfo="true"
	htmlFormAction="iacucProtocolProcedures"
	documentTypeName="IacucProtocolDocument"
	renderMultipart="true"
	showTabButtons="true"
	auditCount="0"
  	headerDispatch="${KualiForm.headerDispatch}"
  	headerTabActive="procedures">

  	<div align="right"><kul:help documentTypeName="${KualiForm.docTypeName}" pageName="Procedures" /></div>
  	
	<div id="workarea">
		<kra-iacuc:protocolProcedureOverviewAndTimeline businessObjectClassName="org.kuali.kra.iacuc.IacucProtocol"/>
		<kra-iacuc:iacucProtocolProcedureLocation/>
		<kul:panelFooter />
	</div>

	<script type="text/javascript">
	   var $j = jQuery.noConflict();
	</script>

	<SCRIPT type="text/javascript">
		var kualiForm = document.forms['KualiForm'];
		var kualiElements = kualiForm.elements;
	</SCRIPT>

	<script language="javascript" src="scripts/kuali_application.js"></script>
	<kul:documentControls transactionalDocument="false" suppressRoutingControls="true" />
</kul:documentPage>
