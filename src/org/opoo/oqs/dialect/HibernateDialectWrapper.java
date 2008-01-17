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
package org.opoo.oqs.dialect;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
public class HibernateDialectWrapper implements Dialect {
    final org.hibernate.dialect.Dialect dialect;
    public HibernateDialectWrapper(org.hibernate.dialect.Dialect dialect) {
        this.dialect = dialect;
    }


    public String appendIdentitySelectToInsert(String sql) {
        return dialect.appendIdentitySelectToInsert(sql);
    }


    public boolean bindLimitParametersFirst() {
        return dialect.bindLimitParametersFirst();
    }


    public boolean bindLimitParametersInReverseOrder() {
        return dialect.bindLimitParametersInReverseOrder();
    }


    public char closeQuote() {
        return dialect.closeQuote();
    }


    public String getIdentityInsertString() {
        return dialect.getIdentityInsertString();
    }


    public String getIdentitySelectString(String table, String column, int type) {
        return dialect.getIdentitySelectString(table, column, type);
    }


    public String getLimitString(String querySelect, int offset, int limit) {
        return dialect.getLimitString(querySelect, offset, limit);
    }


    public String getNoColumnsInsertString() {
        return dialect.getNoColumnsInsertString();
    }


    public String getSelectGUIDString() {
        return dialect.getSelectGUIDString();
    }


    public char openQuote() {
        return dialect.openQuote();
    }


    public boolean supportsIdentityColumns() {
        return dialect.supportsIdentityColumns();
    }


    public boolean supportsInsertSelectIdentity() {
        return dialect.supportsInsertSelectIdentity();
    }


    public boolean supportsLimit() {
        return dialect.supportsLimit();
    }


    public boolean supportsLimitOffset() {
        return dialect.supportsLimitOffset();
    }


    public boolean supportsVariableLimit() {
        return dialect.supportsVariableLimit();
    }

    public boolean useMaxForLimit() {
        return dialect.useMaxForLimit();
    }
}
