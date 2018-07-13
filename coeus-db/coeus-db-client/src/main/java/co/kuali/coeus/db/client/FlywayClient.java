/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package co.kuali.coeus.db.client;

import co.kuali.coeus.data.migration.FlywayMigrator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.sql.DataSource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 * JSON sample config:
 * <p>
 * {
 * "coeusDataSource": {
 * "driverName": "com.mysql.jdbc.Driver",
 * "url": "jdbc:mysql://localhost/kcbnd",
 * "username": "kcbnd",
 * "password": "kcbnd"
 * },
 * "riceDataSource": {
 * "driverName": "com.mysql.jdbc.Driver",
 * "url": "jdbc:mysql://localhost/kcbnd",
 * "username": "kcbnd",
 * "password": "kcbnd"
 * },
 * "applyDemo": true,
 * "applyStaging": true,
 * "grm": true,
 * "enabled": true,
 * "sqlMigrationPath": "co/kuali/coeus/data/migration/sql/mysql",
 * "javaMigrationPath": "co/kuali/coeus/data/migration/custom/coeus",
 * "initVersion": "0"
 * }
 */
public class FlywayClient {
    private static final String COEUS_DATASOURCE = "coeusDataSource";
    private static final String RICE_DATASOURCE = "riceDataSource";
    private static final String DRIVER_NAME = "driverName";
    private static final String URL = "url";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String APPLY_DEMO = "applyDemo";
    private static final String APPLY_STAGING = "applyStaging";
    private static final String GRM = "grm";
    private static final String ENABLED = "enabled";
    private static final String SQL_MIGRATION_PATH = "sqlMigrationPath";
    private static final String JAVA_MIGRATION_PATH = "javaMigrationPath";
    private static final String INIT_VERSION = "initVersion";
    
    private static final Logger LOG = Logger.getLogger(FlywayClient.class.getName());
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
    		initLogging("jul-default.properties");
    		HashMap<String, Object> input = new ObjectMapper().readValue(System.in, HashMap.class);
    	
    		new FlywayClient().apply(input);
    }

    @SuppressWarnings("unchecked")
	protected void apply(Map<String, Object> input) {
		LOG.info("Stating Flyway Migrator");

        final Map<String, String> coeusDataSourceInput = (Map<String, String>) input.get(COEUS_DATASOURCE);
        final String coeusDriverName = coeusDataSourceInput.get(DRIVER_NAME);
        final String coeusUrl = coeusDataSourceInput.get(URL);
        final String coeusUsername = coeusDataSourceInput.get(USERNAME);
        final String coeusPassword = coeusDataSourceInput.get(PASSWORD);
        final Map<String, String> riceDataSourceInput = (Map<String, String>) input.get(RICE_DATASOURCE);
        final String riceDriverName = riceDataSourceInput.get(DRIVER_NAME);
        final String riceUrl = riceDataSourceInput.get(URL);
        final String riceUsername = riceDataSourceInput.get(USERNAME);
        final String ricePassword = riceDataSourceInput.get(PASSWORD);

        final Boolean applyDemo = (Boolean) input.get(APPLY_DEMO);
        final Boolean applyStaging = (Boolean) input.get(APPLY_STAGING);
        final Boolean grm = (Boolean) input.get(GRM);
        final Boolean enabled = (Boolean) input.get(ENABLED);
        final String sqlMigrationPath = (String) input.get(SQL_MIGRATION_PATH);
        final String javaMigrationPath = (String) input.get(JAVA_MIGRATION_PATH);
        final String initVersion = (String) input.get(INIT_VERSION);

        final FlywayMigrator migrator = new FlywayMigrator();
        migrator.setDataSource(retrieveDataSource(coeusDriverName, coeusUrl, coeusUsername, coeusPassword));
        migrator.setRiceDataSource(retrieveDataSource(riceDriverName, riceUrl, riceUsername, ricePassword));
        migrator.setManageRice(true);
        migrator.setEmbeddedMode(false);
        migrator.setApplyDemo(applyDemo);
        migrator.setApplyStaging(applyStaging);
        migrator.setGrm(grm);
        migrator.setEnabled(enabled);
        migrator.setSqlMigrationPath(sqlMigrationPath);
        migrator.setJavaMigrationPath(javaMigrationPath);
        migrator.setInitVersion(initVersion);

        try {
            migrator.migrate();
            LOG.info("SUCCESS");
        } catch (SQLException e) {
            LOG.severe(ExceptionUtils.getStackTrace(e));
            LOG.severe("ERROR");
        }
	}

    private DataSource retrieveDataSource(String driverName, String url, String username, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDefaultAutoCommit(false);
        dataSource.setEnableAutoCommitOnReturn(false);

        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
    
	private static void initLogging(String file) {
		final String fname = System.getProperty("java.util.logging.config.file");
		if (fname == null || !new File(fname).isFile()) {
			// configure default
			try (InputStream in = new BufferedInputStream(FlywayClient.class.getResourceAsStream(file))) {
				LogManager.getLogManager().readConfiguration(in);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}