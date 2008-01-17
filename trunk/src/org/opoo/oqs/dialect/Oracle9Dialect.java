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
 * A SQL dialect for Oracle 9 (uses ANSI-style syntax where possible).
 *
 */
public class Oracle9Dialect extends AbstractDialect {
    public Oracle9Dialect() {
        super();
    }

    public boolean supportsLimit() {
        return true;
    }

    public String getLimitString(String sql, boolean hasOffset) {

        sql = sql.trim();
        boolean isForUpdate = false;
        if (sql.toLowerCase().endsWith(" for update")) {
            sql = sql.substring(0, sql.length() - 11);
            isForUpdate = true;
        }

        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
        if (hasOffset) {
            pagingSelect.append(
                    "select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(sql);
        if (hasOffset) {
            pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
        } else {
            pagingSelect.append(" ) where rownum <= ?");
        }

        if (isForUpdate) {
            pagingSelect.append(" for update");
        }

        return pagingSelect.toString();
    }

    public boolean bindLimitParametersInReverseOrder() {
        return true;
    }

    public boolean useMaxForLimit() {
        return true;
    }

    public boolean forUpdateOfColumns() {
        return true;
    }

    public String getQuerySequencesString() {
        return "select sequence_name from user_sequences";
    }

    public String getSelectGUIDString() {
        return "select rawtohex(sys_guid()) from dual";
    }

}
