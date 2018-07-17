<%--
  ~ Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
  ~ You may use and modify this code under the terms of the Kuali, Inc.
  ~ Pre-Release License Agreement. You may not distribute it.
  ~
  ~ You should have received a copy of the Kuali, Inc. Pre-Release License
  ~ Agreement with this file. If not, please write to license@kuali.co.
  --%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<c:set var="canViewCfdaLookup" value="${KualiForm.cfdaLookupRequired}" scope="request" />
<c:set var="institutionalProposalCfdaAttributes" value="${DataDictionary.InstitutionalProposalCfda.attributes}" />
<h3>
    <span class="subhead-left">CFDA</span>
</h3>
<table id="cfda-table" cellpadding="0" cellspacing="0" summary="CFDA Information">
    <tr>
        <th scope="row">&nbsp;</th>
        <th>
            <div align="center">
                <kul:htmlAttributeLabel attributeEntry="${institutionalProposalCfdaAttributes.cfdaNumber}" useShortLabel="true" noColon="true" />
            </div>
        </th>
        <th>
            <div align="right">
                <kul:htmlAttributeLabel attributeEntry="${institutionalProposalCfdaAttributes.cfdaDescription}" useShortLabel="true" noColon="true" />
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
                    <kul:htmlControlAttribute property="newProposalCfda.cfdaNumber" attributeEntry="${institutionalProposalCfdaAttributes.cfdaNumber}" />
                    <c:if test="${canViewCfdaLookup}">
                        <c:if test="${!readOnly}">
                            <kul:lookup boClassName="org.kuali.kra.award.home.CFDA" fieldConversions="cfdaNumber:newProposalCfda.cfdaNumber,cfdaProgramTitleName:newProposalCfda.cfdaDescription" lookupParameters="newProposalCfda.cfdaNumber:cfdaNumber,newProposalCfda.cfdaDescription:cfdaProgramTitleName" anchor="${tabKey}" />
                        </c:if>
                    </c:if>
                </div>
            </td>
            <td class="infoline">
                <div align="right">
                    <kul:htmlControlAttribute property="newProposalCfda.cfdaDescription" attributeEntry="${institutionalProposalCfdaAttributes.cfdaDescription}" />
                </div>
            </td>
            <td class="infoline">
                <div align=center>
                    <html:image property="methodToCall.addInstitutionalProposalCfda.anchor${tabKey}"
                                src='${ConfigProperties.kra.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton addButton"/>
                </div>
            </td>
        </tr>
        </tbody>
    </c:if>
    <c:forEach var="proposalCfda" items="${KualiForm.document.institutionalProposalList[0].proposalCfdas}" varStatus="status">
        <tr>
            <th width="50" align="center" scope="row" class="infoline">
                <div align="center">${status.index+1}</div>
            </th>
            <td class="infoline">
                <div align="center">
                    <kul:htmlControlAttribute property="document.institutionalProposalList[0].proposalCfdas[${status.index}].cfdaNumber" attributeEntry="${institutionalProposalCfdaAttributes.cfdaNumber}" forceRequired="true"/>
                    <c:if test="${canViewCfdaLookup}">
                        <c:if test="${!readOnly}">
                            <kul:lookup boClassName="org.kuali.kra.award.home.CFDA" fieldConversions="cfdaNumber:document.institutionalProposalList[0].proposalCfdas[${status.index}].cfdaNumber,cfdaProgramTitleName:document.institutionalProposalList[0].proposalCfdas[${status.index}].cfdaDescription" lookupParameters="document.institutionalProposalList[0].proposalCfdas[${status.index}].cfdaNumber:cfdaNumber,document.institutionalProposalList[0].proposalCfdas[${status.index}].cfdaDescription:cfdaProgramTitleName" anchor="${tabKey}" />
                        </c:if>
                        <c:if test="${!readOnly or !empty KualiForm.document.institutionalProposalList[0].proposalCfdas[status.index].cfdaNumber}">
                            <kul:directInquiry boClassName="org.kuali.kra.award.home.CFDA" inquiryParameters="document.institutionalProposalList[0].proposalCfdas[${status.index}].cfdaNumber:cfdaNumber,document.institutionalProposalList[0].proposalCfdas[${status.index}].cfdaDescription:cfdaProgramTitleName" anchor="${tabKey}" />
                        </c:if>
                    </c:if>
                </div>
            </td>
            <td class="infoline">
                <div align="right">
                    <kul:htmlControlAttribute property="document.institutionalProposalList[0].proposalCfdas[${status.index}].cfdaDescription" attributeEntry="${institutionalProposalCfdaAttributes.cfdaDescription}" />
                </div>
            </td>
            <td class="infoline">
                <div align=center>
                    <c:if test="${readOnly}">
                        &nbsp;
                    </c:if>
                    <c:if test="${!readOnly}">
                        <html:image property="methodToCall.deleteInstitutionalProposalCfda.line${status.index}.anchor${currentTabIndex}"
                                    src='${ConfigProperties.kra.externalizable.images.url}tinybutton-delete1.gif' styleClass="tinybutton"/>
                    </c:if>
                </div>
            </td>
        </tr>
    </c:forEach>
</table>