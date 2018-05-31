<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>
<%-- member of AwardNotesAndAttachments.jsp --%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>
<c:set var="tabItemCount" value="0" />
<c:forEach var="awardComment" items="${KualiForm.document.awardList[0].awardComments}">
	<c:if test="${!empty awardComment.comments}">
		<c:set var="tabItemCount" value="${tabItemCount+1}" />
	</c:if>
</c:forEach>

<kul:tabTop tabTitle="Comments (${tabItemCount})" defaultOpen="false" tabErrorKey="document.awardList[0].awardComment[*">
	<div class="tab-container" align="center">
    	<h3>
    		<span class="subhead-left">Comments</span>
    		<span class="subhead-right"><kul:help businessObjectClassName="org.kuali.kra.award.home.AwardComment" altText="help"/></span>
        </h3>
         <c:forEach var="commentType" items="${KualiForm.awardCommentBean.awardCommentScreenDisplayTypes}" varStatus="commentTypeIndex">        	        	
			<kra-a:awardCommentsTypes index="${commentTypeIndex.index}" commentTypeDescription="${commentType.description}" commentTypeCode="${commentType.commentTypeCode}" awardId="${KualiForm.document.award.awardId}"/>
		</c:forEach>
		
		<br/>
		
		 <c:if test="${(!readOnly)}">
			<kra-a:awardSyncButton  scopeNames="COMMENTS_TAB" tabKey="${tabKey}"/>		
		 </c:if>
 	</div>
</kul:tabTop>
