/*
 * $Id$
 *
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opoo.oqs.dialect;

/**
 * A SQL dialect for MySQL.
 *
 */
public class MySQLDialect extends AbstractDialect {
    public MySQLDialect() {
        super();
    }

    public boolean supportsIdentityColumns() {
        return true;
    }

    public String getIdentitySelectString() {
        return "select last_insert_id()";
    }

    public boolean supportsLimit() {
        return true;
    }

    public String getLimitString(String sql, boolean hasOffset) {
        return new StringBuffer(sql.length() + 20)
                .append(sql)
                .append(hasOffset ? " limit ?, ?" : " limit ?")
                .toString();
    }

    public char closeQuote() {
        return '`';
    }

    public char openQuote() {
        return '`';
    }

    public String getSelectGUIDString() {
        return "select uuid()";
    }
}
