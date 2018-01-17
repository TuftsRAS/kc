<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>

<%@ page import="org.kuali.kra.infrastructure.Constants"%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>
<kul:documentPage
	showDocumentInfo="true"
	htmlFormAction="protocolOnlineReviewInactive"
	documentTypeName="IacucProtocolOnlineReviewDocument"
	renderMultipart="true"
	showTabButtons="true"
	auditCount="0"
  	headerDispatch="${KualiForm.headerDispatch}"
  	headerTabActive="onlineReview">
<script type="text/javascript">
    var $j = jQuery.noConflict();
</script>




<c:set var="protocolAttributes" value="${DataDictionary.IacucProtocolDocument.attributes}" />
<c:set var="onlineReviewAttributes" value = "${DataDictionary.IacucProtocolOnlineReview.attributes}"/>
<c:set var="protocolReviewerAttributes" value="${DataDictionary.ProtocolReviewerBean.attributes}" />

<c:set var="readOnly" value = "true"/>

<style type="text/css">
   .compare { color: #666666 }
   .compare td, .compare th { color:#666666; }
</style>




	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<kul:tab tabTitle="Protocol Online Review Basic Fields" defaultOpen="true" tabErrorKey="" >
	
	
	 <table cellpadding="0" cellspacing="0" class="datatable" title="view/edit document overview information" summary="view/edit document overview information">
				<tr>
                	<th width = "25%" class="grid">
                		<div align="right">
                			Reviewer:
                		</div>
                	</th>
                	<td width = "25%" nowrap class="grid">
						<c:out value = "${KualiForm.document.protocolOnlineReview.protocolReviewer.fullName}"/>
					</td>
                <th width = "25%" class="grid">
                	<div align="right">
                		<kul:htmlAttributeLabel attributeEntry="${onlineReviewAttributes.dateRequested}" noColon="false" />
                	</div>
                </th>
                <td width = "25%" class="grid">
					<kul:htmlControlAttribute property="document.protocolOnlineReview.dateRequested" attributeEntry="${onlineReviewAttributes.dateRequested}" datePicker="true" readOnly = "${readOnly}" />
                </td>
              </tr>
              <tr>
				<th width = "25%" class="grid">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${onlineReviewAttributes.protocolOnlineReviewStatusCode}" noColon="false" />
					</div>
				</th>
              	<td width = "25%" class = "grid">
              		<kul:htmlControlAttribute property="document.protocolOnlineReview.protocolOnlineReviewStatus.description" attributeEntry="${onlineReviewAttributes.protocolOnlineReviewStatusCode}" datePicker="false" readOnly="${readOnly}" />
              	</td>
                <th width = "25%" class="grid">
                	<div align="right">
                		<kul:htmlAttributeLabel attributeEntry="${onlineReviewAttributes.dateDue}" noColon="false"  />
                	</div>
                </th>
                <td width = "25%" class="grid" >
                	<kul:htmlControlAttribute property="document.protocolOnlineReview.dateDue" attributeEntry="${onlineReviewAttributes.dateDue}" datePicker="true" readOnly = "${readOnly}" />
                </td>
              </tr>
			  <tr>
              
              	<th width = "25%" class="grid">
                	<div align="right">
                		<kul:htmlAttributeLabel attributeEntry="${onlineReviewAttributes.protocolOnlineReviewDeterminationRecommendationCode}" noColon="false" />
                	</div>
                </th>
                <td width = "25%" class="grid" >
                	<kul:htmlControlAttribute property="document.protocolOnlineReview.protocolOnlineReviewDeterminationRecommendationCode" attributeEntry="${onlineReviewAttributes.protocolOnlineReviewDeterminationRecommendationCode}" datePicker="false" readOnly="${readOnly}" />
                </td>
                <th width = "25%" class="grid">
           			<div align="right">
                		<kul:htmlAttributeLabel attributeEntry="${protocolReviewerAttributes.reviewerTypeCode}" noColon="false" />
                	</div>
                </th>
                <td width = "25%" class="grid" >
                	<kul:htmlControlAttribute property="document.protocolOnlineReview.protocolReviewer.reviewerTypeCode"
		                                                                                  attributeEntry="${protocolReviewerAttributes.reviewerTypeCode}" readOnly = "${readOnly}"/>
				</td>
              </tr>
 			  <tr>
              
              	<th width = "25%" class="grid">
                	<div align="right">
                		Explanation:
                	</div>
                </th>
                <td colspan="3">
	                    <div align="left">
	                     
	                        Online Review can be edited/viewed only if the following conditions are met :
	                        <font color = "red"><p> 
	                        <li>The protocol submission status is 'Submitted to committee' or 'In Agenda'. </li>	                        
	                        <li>You must be an Iacuc Admin or assigned reviewer.</li>
	                        </p></font>
	                        
	                        </div>
                </td>
               </tr>
         	</table>
	
	</kul:tab>

	<kul:routeLog />

<kul:panelFooter />
<%-- <kul:panelFooter /> --%>
	<kul:documentControls 
		transactionalDocument="true"
		suppressRoutingControls="false"
		suppressCancelButton="false"
		extraButtonSource="${extraButtonSource}"
		extraButtonProperty="${extraButtonProperty}"
		extraButtonAlt="${extraButtonAlt}"
		viewOnly="${KualiForm.editingMode['viewOnly']}"
		/>


<script language="javascript">
//enableJavaScript()

$j(document).ready(function() {

	$j("#globalbuttons").find('input').each(function() {
              //alert($j(this).attr("name"));
              if ($j(this).attr("name") != 'methodToCall.close') {
            	  $j(this).hide();
              } 
          });

    });

</script>
</kul:documentPage>
