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
package org.opoo.oqs.jdbc;

import java.sql.Connection;

/**
 * 数据库连接（{@link java.sql.Connection}）管理的接口。
 * 主要针对数据库连接的管理，可以根据需要实现连接池管理等。
 *
 * @author Alex Lin
 * @version 1.0
 */
public interface ConnectionManager {

    /**
     * Fetch the JDBC Connection that this handle refers to.
     * @return Connection
     * @throws DataAccessException
     */
    Connection getConnection() throws DataAccessException;

    /**
     * Release the JDBC Connection that this handle refers to.
     * @param con the JDBC Connection to release
     */
    void releaseConnection(Connection con);
}
