<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<c:set var="institutionalProposalAttributes" value="${DataDictionary.InstitutionalProposal.attributes}" />
<c:set var="institutionalProposalCommentAttributes" value="${DataDictionary.InstitutionalProposalComment.attributes}" />

<c:set var="readOnly" value="${not KualiForm.editingMode['fullEntry']}" scope="request" />
<c:set var="action" value="institutionalProposalHome" />

<kul:tab tabTitle="Institutional Proposal" defaultOpen="false" tabErrorKey="document.institutionalProposalList[0].statusCode,document.institutionalProposalList[0].activityTypeCode,document.institutionalProposalList[0].proposalTypeCode,document.institutionalProposalList[0].title,document.institutionalProposalList[0].currentAwardNumber">
	<!-- Institution -->
<div class="tab-container" align="center">

<h3>
	<span class="subhead-left">Institutional Proposal</span>
	<span class="subhead-right"><kul:help businessObjectClassName="org.kuali.kra.institutionalproposal.home.InstitutionalProposal" altText="help"/></span>
</h3>
<table cellpAdding="0" cellspacing="0" summary="">
  	<tr>
    	<th width="400">
    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.proposalNumber}" /></div>
    	</th>
    	<td>
    		${KualiForm.institutionalProposalDocument.institutionalProposal.proposalNumber}&nbsp;
    	</td>
    	<th width="400">
    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.sequenceNumber}" /></div>
		</th>
    	<td>
    		${KualiForm.institutionalProposalDocument.institutionalProposal.sequenceNumber}&nbsp;
		</td>
  	</tr>
  	<tr>
    	<th width="400">
    		<div align="right">Fiscal Month/Year:</div>
    	</th>
    	<td>
    		${KualiForm.institutionalProposalDocument.institutionalProposal.fiscalMonth}/${KualiForm.institutionalProposalDocument.institutionalProposal.fiscalYear}&nbsp;
    	</td>
    	<th width="400">
    		<div align="right">Created from Prop Log:</div>
    	</th>
    	<td>
    		${KualiForm.institutionalProposalDocument.institutionalProposal.instProposalNumber}&nbsp;
    	</td>
  	</tr>
  	<tr>
    	<th width="400">
    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.currentAwardNumber}" /></div>
		</th>
    	<td>
    		<div align="left"> 
				<kul:htmlControlAttribute property="document.institutionalProposalList[0].currentAwardNumber" attributeEntry="${institutionalProposalAttributes.currentAwardNumber}"/>
				<c:if test="${!readOnly}">
			         <kul:lookup boClassName="org.kuali.kra.award.home.Award" fieldConversions="awardNumber:document.institutionalProposal.currentAwardNumber" anchor="${tabKey}"/>
			    </c:if>
			</div>
    	</td>
    	<th>
    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.statusCode}" /></div>
    	</th>
    	<%--${KualiForm.institutionalProposalDocument.institutionalProposal.statusCode}--%>
    	<c:choose>
			<c:when test="${KualiForm.institutionalProposalDocument.institutionalProposal.statusCode == 2}">
				<td>
 					Funded
 				</td>
			</c:when> 
			<c:otherwise>
 				<td>
    				<kul:htmlControlAttribute property="document.institutionalProposal.statusCode" attributeEntry="${institutionalProposalAttributes.statusCode}" />
				</td>
			</c:otherwise>
		</c:choose>
  	</tr>
  	<tr>
    	<th>
    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.proposalTypeCode}" /></div>
    	</th>
    	<td>
    		<kul:htmlControlAttribute property="document.institutionalProposalList[0].proposalTypeCode" attributeEntry="${institutionalProposalAttributes.proposalTypeCode}" />
		</td>
    	<th>
    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.activityTypeCode}" /></div>
    	</th>
    	<td>
    		<kul:htmlControlAttribute property="document.institutionalProposalList[0].activityTypeCode" attributeEntry="${institutionalProposalAttributes.activityTypeCode}" />
		</td>
  	</tr>
  	<tr>
    	<th>
    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.initialContractAdmin}" /></div>
    	</th>
    	<td>
    	    <kul:inquiry boClassName="org.kuali.coeus.common.framework.person.KcPerson" keyValues="personId=${KualiForm.institutionalProposalDocument.institutionalProposal.initialContractAdmin}" render="true">
    	       <c:out value="${KualiForm.institutionalProposalDocument.institutionalProposal.initialContractAdminUser.userName}" />
    	   </kul:inquiry>&nbsp;
		</td>
    	<th>
    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.createTimestamp}" /></div>
    	</th>
    	<td>
    		${KualiForm.institutionalProposalDocument.institutionalProposal.createTimestamp}&nbsp;
		</td>
  	</tr>
  	<tr>
    	<th>
    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.updateUser}" /></div>
    	</th>
    	<td>
    		${KualiForm.institutionalProposalDocument.institutionalProposal.updateUser}&nbsp;
		</td>
    	<th>
    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.updateTimestamp}" /></div>
    	</th>
    	<td>
    		${KualiForm.institutionalProposalDocument.institutionalProposal.updateTimestamp}&nbsp;
		</td>
  	</tr>
  	<tr>
    	<th>
    		<div align="right">
        		<kul:htmlAttributeLabel attributeEntry="${institutionalProposalAttributes.title}" />
      		</div>
      	</th>
    	<td colspan="3">
        	<table style="border:none; width:100%;">
        		<tr>
            		<td style="border:none; width:100%;">
            			<kul:htmlControlAttribute property="document.institutionalProposalList[0].title" attributeEntry="${institutionalProposalAttributes.title}" />
        			</td>
            	</tr>
        	</table>
    	</td>
  	</tr>
</table>
</div>
<div class="tab-container" align="center">
    <h3>
        <span class="subhead-left">Summary Comments</span>
    </h3>
    <table>
        <th width="100" align="right" scope="row"><div align="center">Add:</div></th>
        <td class="infoline">
             <div align="left">
                 <kul:htmlControlAttribute property="document.institutionalProposalList[0].summaryComment.comments" attributeEntry="${institutionalProposalCommentAttributes.comments}"/>
             </div>
        </td>
    </table>
</div>
</kul:tab>
