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
package org.opoo.oqs.core;

import java.util.ArrayList;
import java.util.List;

import org.opoo.oqs.PreparedStatementBatcher;
import org.opoo.oqs.StatementBatcher;
import org.opoo.oqs.TypedValue;

/**
 *
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class AbstractBatcher implements StatementBatcher,
        PreparedStatementBatcher {
    private final AbstractQueryFactory queryFactory;
    private String sql = null;
    private List list = null;
    private List sqls = null;
    public AbstractBatcher(AbstractQueryFactory queryFactory) {
        sqls = new ArrayList();
	this.queryFactory = queryFactory;
    }

    public AbstractBatcher(AbstractQueryFactory queryFactory, String sql) {
        this.sql = sql;
	this.queryFactory = queryFactory;
        list = new ArrayList();
    }


    public PreparedStatementBatcher addBatch(TypedValue[] typedValues) {
        list.add(typedValues);
        return this;
    }

    public StatementBatcher addBatch(String sql) {
        sqls.add(sql);
        return this;
    }

    protected String getSql() {
        if (queryFactory.debugLevel > 0) {
            System.out.println("[SQL]: " + sql);
        }
        return sql;
    }

    protected String[] getSqls() {
        String[] sqla = (String[]) sqls.toArray(new String[sqls.size()]);
        if (queryFactory.debugLevel > 0) {
	    for(int i = 0 ; i < sqla.length ; i++){
                System.out.println("[SQL" + i + "]: " + sqla[i]);
            }
        }
        return sqla;
    }

    protected List getTypedValuesList() {
        return list;
    }
}
