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
 * A SQL dialect for Microsoft SQL Server 2000
 */
public class SQLServerDialect extends SybaseDialect {
    public SQLServerDialect() {
        super();
    }

    static int getAfterSelectInsertPoint(String sql) {
        int selectIndex = sql.toLowerCase().indexOf("select");
        final int selectDistinctIndex = sql.toLowerCase().indexOf(
                "select distinct");
        return selectIndex + ((selectDistinctIndex == selectIndex) ? 15 : 6);
    }

    public String getLimitString(String querySelect, int offset, int limit) {
        if (offset > 0) {
            throw new UnsupportedOperationException(
                    "sql server has no offset");
        }
        return new StringBuffer(querySelect.length() + 8)
                .append(querySelect)
                .insert(getAfterSelectInsertPoint(querySelect), " top " + limit)
                .toString();
    }

    /**
     * Use <tt>insert table(...) values(...) select SCOPE_IDENTITY()</tt>
     *
     * author <a href="mailto:jkristian@docent.com">John Kristian</a>
     * @param insertSQL String
     * @return String
     */
    public String appendIdentitySelectToInsert(String insertSQL) {
        return insertSQL + " select scope_identity()";
    }

    public boolean supportsLimit() {
        return true;
    }

    public boolean useMaxForLimit() {
        return true;
    }

    public boolean supportsLimitOffset() {
        return false;
    }

    public boolean supportsVariableLimit() {
        return false;
    }

    public char closeQuote() {
        return ']';
    }

    public char openQuote() {
        return '[';
    }

    public String getSelectGUIDString() {
        return "select newid()";
    }
}
