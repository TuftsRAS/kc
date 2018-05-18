<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<td class="content" valign="top">
	  	<channel:messageOfTheDay/>
      <channel:researcherProposals />
      <channel:researcherAwards />
      <channel:researcherNegotiations />
      <channel:researcherPersonnel />
</td>
<td class="content" valign="top">
      <channel:researcherCompliance/>
      <channel:researcherIRBProtocols/>
</td>
<td class="content" valign="top">
      <channel:researcherIACUCProtocols/>
      <channel:researcherWorkflow />
</td>