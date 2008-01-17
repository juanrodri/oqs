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

import org.opoo.oqs.jdbc.ConnectionManager;
import org.opoo.oqs.jdbc.DataAccessException;
import org.opoo.oqs.jdbc.DataSourceAware;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;


/**
 * 简单的实现了ConnectionManager的接口。
 * 使用JdbcUtils管理数据库连接。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @see JdbcUtils
 * @since 1.0
 */
public class SimpleConnectionManager implements ConnectionManager,
        DataSourceAware {
    private DataSource dataSource;
    public SimpleConnectionManager() {
        super();
    }

    public SimpleConnectionManager(DataSource dataSource) {
        setDataSource(dataSource);
        //dataSource.getConnection().setSavepoint("");
    }

    /**
     * 从数据源中简单的取得一个连接。忽略异常。
     *
     * @return Connection
     * @throws DataAccessException
     * @see DataSourceUtils#getConnection
     */
    public Connection getConnection() throws DataAccessException {
        return DataSourceUtils.getConnection(dataSource);
    }

    /**
     * 释放连接。简单地关闭一个连接，忽略异常。
     *
     * @param con Connection
     * @see JdbcUtils#closeConnection
     */
    public void releaseConnection(Connection con) {
        /*
             try
             {
          System.out.println("AutoCommit() = " + con.getAutoCommit());
             }
             catch (SQLException ex)
             {
          ex.printStackTrace();
             }*/
        JdbcUtils.closeConnection(con);
    }

    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }
}
