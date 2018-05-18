/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.kra.award.notification;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.notification.impl.NotificationRendererBase;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.paymentreports.awardreports.reporting.ReportTracking;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AwardReportTrackingDigestNotificationRenderer extends NotificationRendererBase {

    private static final long serialVersionUID = -4990706022002417957L;

    private static final String HEADER_FORMAT_STRING = "<h3>%s</h3>";
    private static final String OVERDUE_HEADER = "OVERDUE";
    private static final String DUE_HEADER = "DUE";
    private static final String PI_NAME_REPLACEMENT_PARAM = "{PI_NAME}";
    private static final String UNIT_NAME_REPLACEMENT_PARAM = "{LEAD_UNIT_NAME}";
    private static final String LIST_DELIMITER = ", ";
    private static final Pattern DIGEST_TABLE_PATTERN = Pattern.compile("(\\{BEGIN_DIGEST_TABLE}(.*)\\{END_DIGEST_TABLE})");

    private LocalDate comparisonDate = LocalDate.now();
    private boolean includeDue = true;
    private boolean includeOverdue = true;
    private Collection<ReportTracking> digestReports = new ArrayList<>();

    @Override
    public String render(String text) {
        // Render digest table using award report tracking renderer for report/award-specific replacement parameters
        String renderedText = replaceSection(text, DIGEST_TABLE_PATTERN, digestTableTemplate ->
            digestReports.stream()
                    .collect(Collectors.groupingBy(this::isOverdue, () -> new TreeMap<Boolean, List<ReportTracking>>(Comparator.reverseOrder()), Collectors.toList()))
                    .entrySet().stream()
                    .map(e -> renderDigestTable(e.getKey(), e.getValue(), digestTableTemplate))
                    .collect(Collectors.joining()));
        // Resolve any remaining replacement parameters, or those that apply to the digest as a whole
        return super.render(renderedText);
    }

    protected String replaceSection(String inputText, Pattern repeatPattern, Function<String, String> replaceFunction) {
        Matcher matcher = repeatPattern.matcher(inputText);
        if (matcher.find()) {
            return inputText.replace(matcher.group(1), replaceFunction.apply(matcher.group(2)));
        }
        return inputText;
    }

    protected String renderDigestTable(boolean isOverdue, List<ReportTracking> reports, String template) {
        String header = String.format(HEADER_FORMAT_STRING, isOverdue ? OVERDUE_HEADER : DUE_HEADER);
        if (isOverdue && includeOverdue || !isOverdue && includeDue) {
            reports.sort(Comparator.comparing(ReportTracking::getPiName)
                    .thenComparing(ReportTracking::getDueDate)
                    .thenComparing(ReportTracking::getAwardNumber)
                    .thenComparing(ReportTracking::getReportClassCode)
                    .thenComparing(ReportTracking::getReportCode));
            return header + new AwardReportTrackingNotificationRenderer(reports).render(template);
        }
        return "";
    }

    protected boolean isOverdue(ReportTracking reportTracking) {
        return reportTracking.getDueDate() != null && reportTracking.getDueDate().toLocalDate().isBefore(comparisonDate);
    }

    @Override
    public Map<String, String> getDefaultReplacementParameters() {
        Map<String, String> params = super.getDefaultReplacementParameters();
        if (CollectionUtils.isNotEmpty(digestReports)) {
            params.put(PI_NAME_REPLACEMENT_PARAM, getAwardReplacementParamList(Award::getPrincipalInvestigatorName));
            params.put(UNIT_NAME_REPLACEMENT_PARAM, getAwardReplacementParamList(Award::getLeadUnitName));
        }
        return params;
    }

    protected String getAwardReplacementParamList(Function<Award, String> awardFunction) {
        return makeListGrammaticallyCorrect(digestReports.stream()
                .map(ReportTracking::getAward)
                .map(awardFunction)
                .distinct()
                .sorted()
                .collect(Collectors.joining(LIST_DELIMITER)));
    }

    protected String makeListGrammaticallyCorrect(String delimitedList) {
        if (StringUtils.isBlank(delimitedList)) {
            return "";
        }
        int numCommas = StringUtils.countMatches(delimitedList, LIST_DELIMITER);
        if (numCommas == 1) {
            delimitedList = delimitedList.replace(LIST_DELIMITER, " and ");
        } else if (numCommas > 1) {
            int lastCommaIndex = delimitedList.lastIndexOf(LIST_DELIMITER);
            delimitedList = delimitedList.substring(0, lastCommaIndex) + LIST_DELIMITER + "and " + delimitedList.substring(lastCommaIndex + 2);
        }
        return delimitedList;
    }

    public void setComparisonDate(LocalDate comparisonDate) {
        this.comparisonDate = comparisonDate;
    }

    public void setIncludeDue(boolean includeDue) {
        this.includeDue = includeDue;
    }

    public void setIncludeOverdue(boolean includeOverdue) {
        this.includeOverdue = includeOverdue;
    }

    public void setDigestReports(Collection<ReportTracking> digestReports) {
        this.digestReports = digestReports;
    }
}
