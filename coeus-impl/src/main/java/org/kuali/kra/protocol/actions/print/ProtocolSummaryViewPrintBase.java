/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.actions.print;


/**
 * This class provides the implementation for printing ProtocolBase Summary View Report. It
 * generates XML that conforms with ProtocolBase Summary View Report XSD, fetches XSL style-sheets
 * applicable to this XML, returns XML and XSL for any consumer that would use
 * this XML and XSls for any purpose like report generation, PDF streaming etc.
 * 
 */
public abstract class ProtocolSummaryViewPrintBase extends ProtocolReportPrintBase {

    private static final long serialVersionUID = 4510549698426529641L;

    @Override
    public String getProtocolPrintType() {
        return ProtocolPrintType.PROTOCOL_SUMMARY_VIEW_REPORT.getProtocolPrintType();
    }

}

