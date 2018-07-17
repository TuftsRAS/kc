<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>

<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<%@ attribute name="subAwardFundingSource" required="true" type="org.kuali.kra.subaward.bo.SubAwardFundingSource" %>
<%@ attribute name="index" required="true" type="java.lang.Integer" %>

<c:set var="fundingSourceAttributes" value="${DataDictionary.Award.attributes}" />
<c:set var="awardCfdaAttributes" value="${DataDictionary.AwardCfda.attributes}" />


<div class="innerTab-head" style="margin-left:60px;">
    <span class="subhead-left">Funding Source Details</span>
    <span class="subhead-right"><kul:help businessObjectClassName="org.kuali.kra.award.home.Award" altText="help"/></span>
</div>
<div style="margin-left:60px;" >
    <table border="0" cellpadding="5" cellspacing="0">
        <tr>
            <th class="infoline" width="15%">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${fundingSourceAttributes.fainId}" skipHelpUrl="true" /></div>
            </th>
            <td>
                <c:out value="${subAwardFundingSource.activeAward.fainId}" />
            </td>
        </tr>
        <tr>
            <th class="infoline">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${fundingSourceAttributes.fedAwardDate}"  skipHelpUrl="true" /></div>
            </th>
            <td>
                <c:out value="${subAwardFundingSource.activeAward.fedAwardDate}" />
            </td>
        </tr>
        <tr>
            <th class="infoline">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${fundingSourceAttributes.obligatedTotal}"  skipHelpUrl="true" /></div>
            </th>
            <td>
                <c:out value="${subAwardFundingSource.activeAward.obligatedTotal}" />
            </td>
        </tr>
        <tr>
            <th class="infoline">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${fundingSourceAttributes.sponsorCode}" skipHelpUrl="true" /></div>
            </th>
            <td>
                <kul:htmlControlAttribute property="document.subAwardList[0].subAwardFundingSourceList[${index}].activeAward.sponsorCode" attributeEntry="${fundingSourceAttributes.sponsorCode}"
                                          onblur="loadSponsorName('document.subAwardList[0].subAwardFundingSourceList[${index}].activeAward.sponsorCode', '${index}sponsorName');" readOnly="true" />

                <html:hidden property="document.subAwardList[0].subAwardFundingSourceList[${index}].activeAward.sponsorCode" />
                <c:if test="${!empty subAwardFundingSource.activeAward.sponsorCode}">
                    <kul:directInquiry boClassName="org.kuali.coeus.common.framework.sponsor.Sponsor" inquiryParameters="document.subAwardList[0].subAwardFundingSourceList[${index}].activeAward.sponsorCode:sponsorCode" anchor="${tabKey}" />
                </c:if>
                <div id="${index}sponsorName.div" >
                    <c:if test="${!empty subAwardFundingSource.activeAward.sponsorCode}">
                        <c:choose>
                            <c:when test="${empty subAwardFundingSource.activeAward.sponsor}">
                                <span style='color: red;'>not found</span>
                            </c:when>
                            <c:otherwise>
                                <c:out value="${subAwardFundingSource.activeAward.sponsor.sponsorName}" />
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </div>
            </td>
        </tr>
        <tr>
            <th class="infoline">
                <div align="right">Prime Sponsor ID:</div>
            </th>
            <td>
                <kul:htmlControlAttribute property="document.subAwardList[0].subAwardFundingSourceList[${index}].activeAward.primeSponsorCode" attributeEntry="${fundingSourceAttributes.primeSponsorCode}"
                                          onblur="loadSponsorName('document.subAwardList[0].subAwardFundingSourceList[${index}].activeAward.primeSponsorCode', '${index}primeSponsorName');" readOnly="true" />

                <html:hidden property="document.subAwardList[0].subAwardFundingSourceList[${index}].activeAward.primeSponsorCode" />
                <c:if test="${!empty subAwardFundingSource.activeAward.primeSponsorCode}">
                    <kul:directInquiry boClassName="org.kuali.coeus.common.framework.sponsor.Sponsor" inquiryParameters="document.subAwardList[0].subAwardFundingSourceList[${index}].activeAward.primeSponsorCode:sponsorCode" anchor="${tabKey}" />
                </c:if>
                <div id="${index}primeSponsorName.div" >
                    <c:if test="${!empty subAwardFundingSource.activeAward.primeSponsorCode}">
                        <c:choose>
                            <c:when test="${empty subAwardFundingSource.activeAward.primeSponsor}">
                                <span style='color: red;'>not found</span>
                            </c:when>
                            <c:otherwise>
                                <c:out value="${subAwardFundingSource.activeAward.primeSponsor.sponsorName}" />
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </div>
            </td>
        </tr>

        <c:forEach var="awardCfda" items="${subAwardFundingSource.activeAward.awardCfdas}" varStatus="status">
            <tr>
                <th class="infoline">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${awardCfdaAttributes.cfdaNumber}" skipHelpUrl="true" /></div>
                </th>
                <td>
                    <c:out value="${awardCfda.cfdaNumber}" />
                </td>
            </tr>
        </c:forEach>

        <tr>
            <th class="infoline">
                <div align="right">Activity Type:</div>
            </th>
            <td>
                <kul:htmlControlAttribute property="document.subAwardList[0].subAwardFundingSourceList[${index}].activeAward.activityTypeCode" attributeEntry="${fundingSourceAttributes.activityTypeCode}" readOnly="true" readOnlyAlternateDisplay="${subAwardFundingSource.activeAward.activityType.description}"/>
                <c:if test="${!empty subAwardFundingSource.activeAward.activityTypeCode}">
                    <kul:directInquiry boClassName="org.kuali.coeus.common.framework.type.ActivityType" inquiryParameters="document.subAwardList[0].subAwardFundingSourceList[${index}].activeAward.activityTypeCode:code" anchor="${tabKey}" />
                </c:if>
            </td>
        </tr>
    </table>
</div>