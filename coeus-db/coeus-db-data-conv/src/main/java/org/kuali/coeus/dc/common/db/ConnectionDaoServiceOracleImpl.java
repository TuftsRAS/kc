/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.dc.common.db;

public class ConnectionDaoServiceOracleImpl extends AbstractConnectionDaoService {

    public static final String JDBC_DRIVER = "oracle.jdbc.OracleDriver";

    @Override
    public String getDriverClassName() {
        return JDBC_DRIVER;
    }
}
