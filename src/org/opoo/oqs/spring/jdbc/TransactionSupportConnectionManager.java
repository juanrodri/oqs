/*
 * $Id$
 *
 * Copyright 2006-2008 Alex Lin. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opoo.oqs.spring.jdbc;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.jdbc.ConnectionManager;
import org.opoo.oqs.jdbc.DataAccessException;
import org.opoo.oqs.jdbc.DataSourceAware;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.transaction.support.
        TransactionSynchronizationManager;


/**
 * Ö§³ÖTransactionµÄConnectionManager.
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class TransactionSupportConnectionManager implements ConnectionManager,
        DataSourceAware {
    private static final Log log = LogFactory.getLog(
            TransactionSupportConnectionManager.class);

    private DataSource dataSource;
    public TransactionSupportConnectionManager() {
    }

    public TransactionSupportConnectionManager(DataSource dataSource) {
        setDataSource(dataSource);
    }


    public Connection getConnection() throws DataAccessException {
        if (dataSource == null) {
            throw new IllegalArgumentException("No DataSource specified");
        }

        Connection conn = (Connection) TransactionSynchronizationManager.
                          getResource(dataSource);
        if (conn != null) {
            log.debug("Get connection from ThreadLocal.");
        } else { //(conn == null)
            conn = DataSourceUtils.getConnection(dataSource);
        }
        return conn;
    }

    public void releaseConnection(Connection con) {
        if (dataSource == null) {
            throw new IllegalArgumentException("No DataSource specified");
        }
        Connection conn = (Connection) TransactionSynchronizationManager.
                          getResource(dataSource);
        if (conn != null && (con == conn || conn.equals(con))) {
            log.debug("Connection in Transaction, do not close.");
        } else {
            JdbcUtils.closeConnection(con);
        }
    }

    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }
}
