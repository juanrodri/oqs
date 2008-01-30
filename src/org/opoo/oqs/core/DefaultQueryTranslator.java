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
import org.opoo.util.*;
import org.opoo.oqs.*;

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
	String lsql = queryString.toLowerCase();
        int pos = lsql.indexOf("from");
        if (pos == -1) {
            select = queryString.substring(7).trim();
        } else {
            select = queryString.substring(7, pos);
	    /**
	     * ÅÐ¶Ï¶Ô³ÆÐÔ
	     */
	    while (StringUtils.countUnquoted(select, '(') !=
                   StringUtils.countUnquoted(select, ')') && pos != -1) {
                pos = lsql.indexOf("from", pos + 1);
                if (pos == -1) {
                    throw new QueryException("Invalid query string", queryString);
                }
		select = queryString.substring(7, pos);
            }
	    select = select.trim();
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


    private int findFromPosition2(String sql, String select, int pos) {
        int cc = 1;
        int p = select.indexOf("(");
        if (p != -1 &&
            StringUtils.countUnquoted(select, '(') !=
            StringUtils.countUnquoted(select, ')')) {
            char[] chars = sql.toCharArray();
            int i = p + 8;
            while (cc != 0 && i < chars.length) {
                if (chars[i] == '(') {
                    cc++;
                } else if (chars[i] == ')') {
                    cc--;
                }
                i++;
            }
            if (cc != 0) {
                throw new QueryException("Invalid query string", sql);
            }
            pos = sql.toLowerCase().indexOf("from", i);
        }
	return pos;
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


    public static void main(String[] args) {
        String sql = "select distinct new list(dcid, qid,  (select cnt from v_jxpg_dc_answers where dcid=o.dcid and qid=o.qid and result='1') as a1, (select cnt from v_jxpg_dc_answers where dcid=o.dcid and qid=o.qid and result='2') as a2, (select cnt from v_jxpg_dc_answers where dcid=o.dcid and qid=o.qid and result='3') as a3, (select cnt from v_jxpg_dc_answers where dcid=o.dcid and qid=o.qid and result='4') as a4, (select cnt from v_jxpg_dc_answers where dcid=o.dcid and qid=o.qid and result='5') as a5, (select cnt from v_jxpg_dc_answers where dcid=o.dcid and qid=o.qid and result='6') as a6 ) from v_jxpg_dc_answers o where o.dcid=? order by o.qid";
        DefaultQueryTranslator sqt = new DefaultQueryTranslator(sql);
        //System.out.println(sqt.sql);

        String select = "distinct new list(dcid, qid,  (select cnt ";
        System.out.println(select);
        System.out.println(sql);

        ///1.
        long start = System.currentTimeMillis();
        select = "distinct new list(dcid, qid,  (select cnt ";
        int pos = 10;
        String lsql = sql.toLowerCase();
        while (StringUtils.countUnquoted(select, '(') !=
               StringUtils.countUnquoted(select, ')') && pos != -1) {
            pos = lsql.indexOf("from", pos + 1);
            if (pos == -1) {
                throw new QueryException("Invalid query string", sql);
            }
            select = sql.substring(7, pos);
        }
        select = select.trim();
        String from = sql.substring(pos);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(select);
        System.out.println(from);

        //2.
        start = System.currentTimeMillis();
        select = "distinct new list(dcid, qid,  (select cnt ";
        pos = 10;
        pos = sqt.findFromPosition2(sql, select, pos);
        select = sql.substring(7, pos);
        from = sql.substring(pos);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(select);
        System.out.println(from);
    }
}
