<%--
  ~ Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
  ~ You may use and modify this code under the terms of the Kuali, Inc.
  ~ Pre-Release License Agreement. You may not distribute it.
  ~
  ~ You should have received a copy of the Kuali, Inc. Pre-Release License
  ~ Agreement with this file. If not, please write to license@kuali.co.
  --%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<c:set var="awardCfdaAttributes" value="${DataDictionary.AwardCfda.attributes}" />
<h3>
    <span class="subhead-left">CFDA</span>
</h3>
<table id="cfda-table" cellpadding="0" cellspacing="0" summary="CFDA Information">
    <tr>
        <th scope="row">&nbsp;</th>
        <th>
            <div align="center">
                <kul:htmlAttributeLabel attributeEntry="${awardCfdaAttributes.cfdaNumber}" useShortLabel="true" noColon="true" />
            </div>
        </th>
        <th>
            <div align="right">
                <kul:htmlAttributeLabel attributeEntry="${awardCfdaAttributes.cfdaDescription}" useShortLabel="true" noColon="true" />
            </div>
        </th>
        <th>
            <div align="center">Actions</div>
        </th>
    </tr>
    <c:if test="${!readOnly}">
        <tbody class="addline">
        <tr>
            <th width="50" align="center" scope="row"><div align="center">Add:</div></th>
            <td class="infoline">
                <div align="center">
                    <kul:htmlControlAttribute property="newAwardCfda.cfdaNumber" attributeEntry="${awardCfdaAttributes.cfdaNumber}" />
                    <kra:section permission="viewChartOfAccountsElement">
                        <c:if test="${!readOnly}">
                            <kul:lookup boClassName="org.kuali.kra.award.home.CFDA" fieldConversions="cfdaNumber:newAwardCfda.cfdaNumber,cfdaProgramTitleName:newAwardCfda.cfdaDescription" lookupParameters="newAwardCfda.cfdaNumber:cfdaNumber,newAwardCfda.cfdaDescription:cfdaProgramTitleName" anchor="${tabKey}" />
                        </c:if>
                    </kra:section>
                </div>
            </td>
            <td class="infoline">
                <div align="right">
                    <kul:htmlControlAttribute property="newAwardCfda.cfdaDescription" attributeEntry="${awardCfdaAttributes.cfdaDescription}" />
                </div>
            </td>
            <td class="infoline">
                <div align=center>
                    <html:image property="methodToCall.addAwardCfda.anchor${tabKey}"
                                src='${ConfigProperties.kra.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton addButton"/>
                </div>
            </td>
        </tr>
        </tbody>
    </c:if>
    <c:forEach var="awardCfda" items="${KualiForm.document.awardList[0].awardCfdas}" varStatus="status">
        <tr>
            <th width="50" align="center" scope="row" class="infoline">
                <div align="center">${status.index+1}</div>
            </th>
            <td class="infoline">
                <div align="center">
                    <kul:htmlControlAttribute property="document.awardList[0].awardCfdas[${status.index}].cfdaNumber" attributeEntry="${awardCfdaAttributes.cfdaNumber}" forceRequired="true"/>
                    <kra:section permission="viewChartOfAccountsElement">
                        <c:if test="${!readOnly}">
                            <kul:lookup boClassName="org.kuali.kra.award.home.CFDA" fieldConversions="cfdaNumber:document.awardList[0].awardCfdas[${status.index}].cfdaNumber,cfdaProgramTitleName:document.awardList[0].awardCfdas[${status.index}].cfdaDescription" lookupParameters="document.awardList[0].awardCfdas[${status.index}].cfdaNumber:cfdaNumber,document.awardList[0].awardCfdas[${status.index}].cfdaDescription:cfdaProgramTitleName" anchor="${tabKey}" />
                        </c:if>
                        <c:if test="${!readOnly or !empty KualiForm.document.awardList[0].awardCfdas[status.index].cfdaNumber}">
                            <kul:directInquiry boClassName="org.kuali.kra.award.home.CFDA" inquiryParameters="document.awardList[0].awardCfdas[${status.index}].cfdaNumber:cfdaNumber,document.awardList[0].awardCfdas[${status.index}].cfdaDescription:cfdaProgramTitleName" anchor="${tabKey}" />
                        </c:if>
                    </kra:section>
                </div>
            </td>
            <td class="infoline">
                <div align="right">
                    <kul:htmlControlAttribute property="document.awardList[0].awardCfdas[${status.index}].cfdaDescription" attributeEntry="${awardCfdaAttributes.cfdaDescription}" />
                </div>
            </td>
            <td class="infoline">
                <div align=center>
                    <c:if test="${readOnly}">
                        &nbsp;
                    </c:if>
                    <c:if test="${!readOnly}">
                        <html:image property="methodToCall.deleteAwardCfda.line${status.index}.anchor${currentTabIndex}"
                                    src='${ConfigProperties.kra.externalizable.images.url}tinybutton-delete1.gif' styleClass="tinybutton"/>
                    </c:if>
                </div>
            </td>
        </tr>
    </c:forEach>
</table>