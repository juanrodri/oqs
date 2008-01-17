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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 抽象类，实现了<tt>Dialect</tt>大部分功能。
 *
 */
public abstract class AbstractDialect implements Dialect {
    protected static final Log log = LogFactory.getLog(Dialect.class);

    public AbstractDialect() {
        log.info("Using dialect: " + this.getClass());
    }

    public boolean bindLimitParametersFirst() {
        return false;
    }

    public boolean bindLimitParametersInReverseOrder() {
        return false;
    }


    public String getLimitString(String querySelect, boolean hasOffset) {
        throw new UnsupportedOperationException("paged queries not supported");
    }

    public String getLimitString(String querySelect, int offset, int limit) {
        return getLimitString(querySelect, offset > 0);
    }


    public boolean supportsLimit() {
        return false;
    }


    public boolean supportsLimitOffset() {
        return supportsLimit();
    }


    public boolean supportsVariableLimit() {
        return supportsLimit();
    }


    public boolean useMaxForLimit() {
        return false;
    }

    public boolean supportsIdentityColumns() {
        return false;
    }

    protected String getIdentitySelectString() {
        throw new UnsupportedOperationException(
                "Dialect does not support identity key generation");
    }


    public String getIdentitySelectString(String table, String column, int type) {
        return getIdentitySelectString();
    }

    public String getSelectGUIDString() {
        throw new UnsupportedOperationException(
                "dialect does not support GUIDs");
    }


    public char openQuote() {
        return '"';
    }

    public char closeQuote() {
        return '"';
    }

    public String getIdentityInsertString() {
        return null;
    }

    public String getNoColumnsInsertString() {
        return "values ( )";
    }

    public boolean supportsInsertSelectIdentity() {
        return false;
    }

    public String appendIdentitySelectToInsert(String insertString) {
        return insertString;
    }
}
