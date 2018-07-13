/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.krms;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;


public class StoredFunctionDao extends PlatformAwareDaoBaseOjb {
    private static final Logger LOG = LogManager.getLogger(StoredFunctionDao.class);
    private DataSource dataSource;
    public DataSource getDataSource() {
        return dataSource;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public String executeFunction(final String functionName,final List<Object> paramValues) {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        String result = jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String paramSyntaxString = "";
                int paramCount = paramValues.size();
                for (int i = 0; i < paramCount; i++) {
                    if(i==0) paramSyntaxString+="(?";
                    else if(i==paramCount-1) paramSyntaxString+=",?)";
                    else paramSyntaxString+=",?";
                }
                if(paramCount==1) paramSyntaxString+=")";
                CallableStatement cs = con.prepareCall("{ ? = call "+functionName+paramSyntaxString+"}");
                cs.registerOutParameter(1, Types.VARCHAR); 
                for (int i = 0; i < paramValues.size(); i++) {
                    cs.setObject(i+2,paramValues.get(i));  
                }
                return cs;
            }
        }, new CallableStatementCallback<String>() {
            @Override
            public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                cs.execute();
                String result = cs.getString(1);
                return result; 
            }
            
        });
        LOG.debug(functionName+" result: "+result); 
        return result;
    }

}
