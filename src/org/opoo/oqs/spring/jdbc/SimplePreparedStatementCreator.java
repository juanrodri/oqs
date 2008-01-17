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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;


/**
 * 简单地实现了<code>PreparedStatementCreator</code>和SqlProvider接口的功能。
 *
 * <p>实现使用指定SQL创建PreparedStatement的功能，创建代码例如：
 * <pre>
 * PreparedStatementCreator psc = new SimplePreparedStatementCreator(sql);
 * </pre>
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since 1.0
 */
public class SimplePreparedStatementCreator implements PreparedStatementCreator,
        SqlProvider {
    private final String sql;
    public SimplePreparedStatementCreator(String sql) {
        this.sql = sql;
    }

    /**
     * Create a statement in this connection.
     *
     * @param con Connection to use to create statement
     * @return a prepared statement
     * @throws SQLException there is no need to catch SQLExceptions that may be
     *   thrown in the implementation of this method. The JdbcTemplate class
     *   will handle them.
     */
    public PreparedStatement createPreparedStatement(Connection con) throws
            SQLException {
        return con.prepareStatement(sql);
    }

    public String getSql() {
        return sql;
    }
}
