/*
 * Copyright 2006-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.s2s;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.KraTestBase;
/**
 * 
 * This class is to test the database connectivity for coeus s2s engine.
 */
public class KualiDataSourceConnectionManagerTest extends KraTestBase {
    @Before
	public void setUp() throws Exception{
		super.setUp();
	}
    @After
	public void tearDown() throws Exception {
		super.tearDown();
	}
    @Test
    public final void freeDatabaseConnectionTest() throws Exception{
        KualiDataSourceConnectionManager cnnMgr = new KualiDataSourceConnectionManager();
        Connection conn = cnnMgr.getDatabaseConnection();
        assertNotNull(conn);
        cnnMgr.freeDatabaseConnection(conn);
    }
}
