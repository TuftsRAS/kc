<%--
 Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 You may use and modify this code under the terms of the Kuali, Inc.
 Pre-Release License Agreement. You may not distribute it.
 You should have received a copy of the Kuali, Inc. Pre-Release License
 Agreement with this file. If not, please write to license@kuali.co.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<%@ attribute name="bean" required="true" type="org.kuali.coeus.common.questionnaire.framework.core.QuestionnaireHelperBase" %>
<%@ attribute name="property" required="true" %>
<%@ attribute name="questionIndex" required="true" %>
<%@ attribute name="answerHeaderIndex" required="true" %>
<%@ attribute name="showError" required="false" %>

<c:set var="fieldName" value="${property}.answerHeaders[${answerHeaderIndex}].answers[${questionIndex}].answer" />
<c:set var="answerValidationError" value="false"/>
<c:forEach items="${ErrorPropertyList}" var="key">
    <c:if test="${key eq fieldName}">
        <c:set var="answerValidationError" value="true"/>
    </c:if>
</c:forEach>
<div class="answer">
<c:if test="${showError}">
    <div style="color:red">Mandatory Question not complete.</div>
</c:if>
<c:choose>
    <c:when test="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question.questionTypeId == 1}" >
        <kra-questionnaire:yesNoQuestion question="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question}"
            answer="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex]}" questionIndex="${questionIndex}"
            bean = "${bean}" property = "${property}" answerHeaderIndex = "${answerHeaderIndex}"
            answerValidationError = "${answerValidationError}"/>
    </c:when>
    <c:when test="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question.questionTypeId == 2}" >
        <kra-questionnaire:yesNoNaQuestion question="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question}"
            answer="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex]}" questionIndex="${questionIndex}"
            bean = "${bean}" property = "${property}" answerHeaderIndex = "${answerHeaderIndex}"
            answerValidationError = "${answerValidationError}"/>
    </c:when>
    <c:when test="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question.questionTypeId == 3}" >
        <kra-questionnaire:numberQuestion question="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question}"
            answer="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex]}" questionIndex="${questionIndex}"
            bean = "${bean}" property = "${property}" answerHeaderIndex = "${answerHeaderIndex}"
            answerValidationError = "${answerValidationError}"/>
    </c:when>
    <c:when test="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question.questionTypeId == 4}" >
        <kra-questionnaire:dateQuestion question="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question}"
            answer="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex]}" questionIndex="${questionIndex}"
            bean = "${bean}" property = "${property}" answerHeaderIndex = "${answerHeaderIndex}"
            answerValidationError = "${answerValidationError}"/>
    </c:when>
    <c:when test="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question.questionTypeId == 5}" >
        <kra-questionnaire:textQuestion question="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question}"
            answer="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex]}" questionIndex="${questionIndex}"
            bean = "${bean}" property = "${property}" answerHeaderIndex = "${answerHeaderIndex}"
            answerValidationError = "${answerValidationError}"/>
    </c:when>
    <c:when test="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question.questionTypeId == 6}" >
        <c:choose>
            <c:when test = "${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question.lookupClass == 'org.kuali.coeus.common.framework.custom.arg.ArgValueLookup'}">
                <c:choose>
                    <c:when test="${krafn:getParameterValueAsBoolean('KC-GEN', 'All', 'Display_KNS_Arg_Value_Lookups_As_Dropdowns')}">
                        <kra:argValueLookupOptions property="${fieldName}" argName="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question.lookupReturn}"
                                                   currentValue="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].answer}" styleClass="Qanswer answer questionnaireAnswer" readOnly="false"/>
                    </c:when>
                    <c:otherwise>
                        <kra-questionnaire:argValueLookupQuestion question="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question}"
                                                                  answer="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex]}" questionIndex="${questionIndex}"
                                                                  bean="${bean}" property="${property}" answerHeaderIndex = "${answerHeaderIndex}"
                                                                  answerValidationError = "${answerValidationError}"/>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <kra-questionnaire:lookupQuestion question="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question}"
                    answer="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex]}" questionIndex="${questionIndex}"
                    bean="${bean}" property="${property}" answerHeaderIndex = "${answerHeaderIndex}"
                    answerValidationError = "${answerValidationError}"/>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question.questionTypeId == 100}" >
        <kra-questionnaire:multipleChoiceQuestion question="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex].question}"
            answer="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex]}" questionIndex="${questionIndex}"
            bean = "${bean}" property = "${property}" answerHeaderIndex = "${answerHeaderIndex}"
            answerValidationError = "${answerValidationError}"/>
    </c:when>
</c:choose>
</div>

