<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<c:set var="action" value="protocolProtocolActions" />

<kra:permission value="${KualiForm.actionHelper.canManageReviewComments}">
      <kra-irb-action:reviewComments bean="${KualiForm.actionHelper.protocolManageReviewCommentsBean.reviewCommentsBean}"
              property="actionHelper.protocolManageReviewCommentsBean.reviewCommentsBean"
              action="${action}"
              taskName="protocolManageReviewComments" 
              tabCustomTitle="Manage Review Comments" 
              methodToCall="manageComments" />
      <kra-irb-action:reviewAttachments bean="${KualiForm.actionHelper.protocolManageReviewCommentsBean.reviewAttachmentsBean}"
              property="actionHelper.protocolManageReviewCommentsBean.reviewAttachmentsBean"
              action="${action}"
              taskName="protocolManageReviewComments" 
              tabCustomTitle="Manage Review Attachments" 
              methodToCall="manageAttachments" />
</kra:permission>
