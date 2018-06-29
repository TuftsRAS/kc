<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>
<%@ attribute name="fullName" required="true"%>
<%@ attribute name="fieldCount" required="true"%>
<%@ attribute name="excludeInactive" required="false" %>
<%@ attribute name="customAttributeGroups" required="true" type="java.util.Map" %>
<%@ attribute name="customDataList" required="true" type="java.util.List" %>
<%@ attribute name="customDataListPrefix" required="true" %>
<%@ attribute name="readOnly" required="false" %>


<c:if test="${empty excludeInactive}" >
	<c:set var="excludeInactive" value="false" />
</c:if>
<c:set var="displayArgValuesAsDropdowns" value="${krafn:getParameterValueAsBoolean('KC-GEN', 'Document', 'Display_KNS_Arg_Value_Lookups_As_Dropdowns')}" />

<c:choose>
		<c:when test="${fn:length(fullName) > 90}">
 					<c:set var="displayName" value="${fn:substring(fullName, 0, 90)}..."/>
		</c:when> 
		<c:otherwise>
 					<c:set var="displayName" value="${fullName}"/>
		</c:otherwise>

</c:choose>

<div class="tab-container" align="center">
	<h3>
		<span class="subhead-left">${displayName}</span>
		<span class="subhead-right"><kul:help businessObjectClassName="org.kuali.coeus.common.framework.custom.attr.CustomAttribute" altText="help"/></span>
	</h3>
	<div align="left" style="padding:12px;"><strong> Full Group Name: </strong>${fullName}</div>
	<table cellpadding=0 cellspacing="0" class="result-table">
		<c:forEach items="${customAttributeGroups[fullName]}" var="customAttributeDocument" varStatus="status">
		<c:if test="${(excludeInactive eq false) or (excludeInactive eq true && customAttributeDocument.active eq true)}">
			<tr class="datatable">
				<th  align="right">
					<c:if test="${customAttributeDocument.required}">*&nbsp;</c:if>${customAttributeDocument.customAttribute.label}:
				</th>
				<td width="45%">
				<c:forEach var="customAttribute" items="${customDataList}" varStatus="status"> 
					<c:if test="${customAttribute.id == customAttributeDocument.id}">
						<c:set var="customAttributeIndex" value="${status.index}" />
						<c:set var="customAttributeValue" value="${customAttribute.value}" />
						<c:set var="customAttributeId" value="${customDataListPrefix}[${customAttributeIndex}].value" />
					</c:if>
				</c:forEach>
                        
          	  <c:set var="customAttributeErrorStyle" value="" scope="request"/>
				<c:forEach items="${ErrorPropertyList}" var="key">
				    <c:if test="${key eq customAttributeId}">
					  <c:set var="customAttributeErrorStyle" value="border-color: red" scope="request"/>
				    </c:if>
			     </c:forEach>
					
				<c:if test="${empty customAttributeErrorStyle}" >			
					<c:forEach items="${AuditErrors}" var="cluster">
						<c:forEach items="${cluster.value.auditErrorList}" var="audit">
						    <c:if test="${audit.errorKey eq customAttributeId}">
							  <c:set var="customAttributeErrorStyle" value="border-color: red" scope="request"/>
						    </c:if>
						</c:forEach>
					</c:forEach>
				</c:if>

				<c:set var="showTextField" value="${(customAttributeDocument.customAttribute.lookupClass ne 'org.kuali.coeus.common.framework.custom.arg.ArgValueLookup' or !displayArgValuesAsDropdowns) and customAttributeDocument.customAttribute.customAttributeDataType.description != 'Boolean'}" />
				<c:choose>
                	<c:when test="${readOnly and showTextField}">
                		<c:out value="${fn:escapeXml(customAttributeValue)}" />
                	</c:when>
                	<c:otherwise>
                		${kfunc:registerEditableProperty(KualiForm, customAttributeId)}

						<c:if test="${showTextField}">
                        	<input size="60" id="${customAttributeId}" type="text" name="${customAttributeId}" value='${fn:escapeXml(customAttributeValue)}' style="${customAttributeErrorStyle}" />
						</c:if>

						<c:if test="${not empty customAttributeDocument.customAttribute.lookupClass}">
						 <c:choose>
						   <c:when test="${customAttributeDocument.customAttribute.lookupClass eq 'org.kuali.coeus.common.framework.custom.arg.ArgValueLookup'}">
							   <c:choose>
								   <c:when test="${krafn:getParameterValueAsBoolean('KC-GEN', 'Document', 'Display_KNS_Arg_Value_Lookups_As_Dropdowns')}">
									   <kra:argValueLookupOptions property="${customAttributeId}" argName="${customAttributeDocument.customAttribute.lookupReturn}" currentValue="${customAttributeValue}" readOnly="${readOnly}" anchor="${tabKey}" />
								   </c:when>
								   <c:otherwise>
									   <kul:lookup boClassName="${customAttributeDocument.customAttribute.lookupClass}"
												   lookupParameters="'${customAttributeDocument.customAttribute.lookupReturn}':argumentName"
												   readOnlyFields="argumentName" fieldConversions="value:${customAttributeId},"
												   fieldLabel="${customAttributeDocument.customAttribute.label}" anchor="${tabKey}" />
								   </c:otherwise>
							   </c:choose>
						   </c:when>
						   <c:otherwise>						   
							<kul:lookup boClassName="${customAttributeDocument.customAttribute.lookupClass}" fieldConversions="${customAttributeDocument.customAttribute.lookupReturn}:${customAttributeId}," fieldLabel="${customAttributeDocument.customAttribute.label}"  anchor="${tabKey}"/>
					       </c:otherwise>
					     </c:choose>
						</c:if>
					
						<c:if test="${customAttributeDocument.customAttribute.customAttributeDataType.description == 'Date'}">
				            <img src="${ConfigProperties.kr.externalizable.images.url}cal.gif" id="${customAttributeId}_datepicker" style="cursor: pointer;"
				             title="Date selector" alt="Date selector"
				             onmouseover="this.style.backgroundColor='red';" onmouseout="this.style.backgroundColor='transparent';" />
					        <script type="text/javascript">
					            Calendar.setup(
					                   {
					                      inputField : "${customAttributeId}", // ID of the input field
					                      ifFormat : "%m/%d/%Y", // the date format
					                      button : "${customAttributeId}_datepicker" // ID of the button
					                    }
					             );
					        </script>
						</c:if>

						<c:if test="${customAttributeDocument.customAttribute.customAttributeDataType.description == 'Boolean'}">
							<input type="radio" class="Custom Data answer QanswerYesNo" name="${customAttributeId}" value="Yes"
								${customAttributeValue == 'Yes' ? "checked='true'" : ''} ${readOnly == 'true' ? "disabled" : ''} />Yes
							<input type="radio" class="Custom Data answer QanswerYesNo" name="${customAttributeId}" value="No"
								${customAttributeValue == 'No' ? "checked='true'" : ''} ${readOnly == 'true' ? "disabled" : ''} />No
						</c:if>

					</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:if>
		</c:forEach>
	</table>
</div>
