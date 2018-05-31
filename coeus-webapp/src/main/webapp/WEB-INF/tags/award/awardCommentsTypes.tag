<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>
<%@ include file="/WEB-INF/jsp/award/awardTldHeader.jsp"%>
<%@ attribute name="index" description="Index" required="true" %>
<%@ attribute name="commentTypeDescription" description="Comment Type Description" required="true" %>
<%@ attribute name="commentTypeCode" description="Award Comment Type Code" required="true" %>
<%@ attribute name="awardId" description="Award ID" required="true" %>
<c:set var="action" value="awardNotesAndAttachments" />

<c:set var="commentAttributes" value="${DataDictionary.AwardComment.attributes}" />
<c:set var="commentMethodName" value="${fn:replace(commentTypeDescription,' ','')}"/>
    <c:set var="commentIndex" value="-1"/>
    <c:set var="emptyComment" value="false" />
    <c:forEach var="awardDocument" items="${KualiForm.document.awardList[0].awardComments}" varStatus="status">
        <c:if test="${KualiForm.document.awardList[0].awardComments[status.index].commentTypeCode == commentTypeCode}">
        	<c:set var="commentIndex" value="${status.index}"/>
			<c:if test="${KualiForm.document.awardList[0].awardCommentHistoryFlags[status.index]}">
				<c:set var="commentEntered" value="true"/>
			</c:if>
        </c:if>
    </c:forEach>
	<c:if test = "${commentIndex gt -1}">
		<kul:innerTab parentTab="Comments" defaultOpen="false" tabTitle="${commentTypeDescription}" tabErrorKey="" >
			<table>
			<tr>
        		<th width="1300" align="left" scope="row"><div align="left">Comments</div></th>
	        	<th align="left" scope="row"><div align="center">Actions</div></th>
    	    </tr>	
			<tr>
        		<td class="infoline">
            		<div align="left"> 
            			<kul:htmlControlAttribute property="${docAward}.awardComment[${commentIndex}].comments" attributeEntry="${commentAttributes.comments}"/>
					</div>	 
    	        </td>	     	 
        	    <td>
					<c:choose>
    	        		<c:when test="${!commentEntered}">
	    	        		&nbsp;
	        	    	</c:when>
	            		<c:otherwise>
            				<div align="center">
            					<a href="${pageContext.request.contextPath}/awardNotesAndAttachments.do?command=redirectAwardCommentHistoryForPopup&awardCommentTypeCode=${commentTypeCode}&awardId=${awardId}" target="_blank" >
    							<img alt="View History" src="${ConfigProperties.kra.externalizable.images.url}tinybutton-viewhistory.gif" styleClass="tinybutton" /></a>
									<c:if test="${KualiForm.syncMode}">
		 								<html:image property="methodToCall.syncComment.awardCommentIdx${commentIndex}.anchor${currentTabIndex}"
 											src='${ConfigProperties.kra.externalizable.images.url}tinybutton-hierarchysync.gif' alt="sync" styleClass="tinybutton" disabled="${readOnly}"/>
									</c:if>				        
    						</div>
						</c:otherwise>
					</c:choose>
	    		</td>
    		</tr>
        	</table>
  		</kul:innerTab>	
  	</c:if>
