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

import org.opoo.oqs.Mapper;
import org.opoo.util.Assert;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
public class DefaultQueryTranslator implements QueryTranslator {
    private static final SelectParser selectParser = new DefaultSelectParser();
    private static final String DISTINCT = "distinct ";
    private final String queryString;
    private final String sql;
    private final Mapper mapper;
    public DefaultQueryTranslator(final String queryString) {
        this(queryString, null);
    }

    public DefaultQueryTranslator(final String queryString,
                                  ClassLoader beanClassLoader) {
        Assert.notNull(queryString, "Translator cannot accept a null string");
        String select = "";
        String from = "";
        int pos = queryString.toLowerCase().indexOf("from");
        if (pos == -1) {
            select = queryString.substring(7).trim();
        } else {
            select = queryString.substring(7, pos).trim();
            from = queryString.substring(pos);
        }

        //distinct
        boolean distinct = select.toLowerCase().startsWith(DISTINCT);
        if (distinct) {
            select = select.substring(9).trim();
        }

        //
        this.queryString = queryString;
        //PropertyMapper pmapper = PropertyMapperUtils.parsePropertyMapper(select);
        //mapper = pmapper;
        mapper = selectParser.parse(select, beanClassLoader);
        sql = "select " + (distinct ? DISTINCT : "") + mapper.toString() + " " +
              from;
    }


    public String getQueryString() {
        return queryString;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public String getSQLString() {
        return sql;
    }
}
