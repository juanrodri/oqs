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
 * 描述不同数据库方言的接口。
 * 不同的数据库产品使用不同的函数、分割符、和SQL分页形式。
 * 目前实现了与数据查询分页相关的部分。
 *
 */
public interface Dialect {

    //////////////////////////////////////
    //分页有关
    /////////////////////////////////////
    /**
     * Does this <tt>Dialect</tt> have some kind of <tt>LIMIT</tt> syntax?
     * @return boolean
     */
    boolean supportsLimit();

    /**
     * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
     * of a total number of returned rows?
     * @return boolean
     */
    boolean useMaxForLimit();

    /**
     * Does this dialect support an offset?
     * @return boolean
     */
    boolean supportsLimitOffset();

    /**
     * Does the <tt>LIMIT</tt> clause come at the start of the
     * <tt>SELECT</tt> statement, rather than at the end?
     *
     * @return true if limit parameters should come before other parameters
     */
    boolean bindLimitParametersFirst();

    /**
     *
     * @return boolean
     */
    boolean supportsVariableLimit();

    /**
     * Does the <tt>LIMIT</tt> clause specify arguments in the "reverse" order
     * limit, offset instead of offset, limit?
     *
     * @return true if the correct order is limit, offset
     */
    boolean bindLimitParametersInReverseOrder();

    /**
     * Add a <tt>LIMIT</tt> clause to the given SQL <tt>SELECT</tt>
     *
     * @param querySelect String
     * @param offset int
     * @param limit int
     * @return String the modified SQL
     */
    String getLimitString(String querySelect, int offset, int limit);

    /**
     * Does this dialect support identity column key generation?
     * @return boolean
     */
    boolean supportsIdentityColumns();

    /**
     * The syntax that returns the identity value of the last insert, if
     * identity column key generation is supported.
     *
     * @param table String
     * @param column String
     * @param type int
     * @return String
     */
    String getIdentitySelectString(String table, String column, int type);

    /**
     * 返回各类数据库生成GUID的SQL。如果数据库不支持GUID，则抛出
     * UnsupportOperationException.
     * @return String
     */
    String getSelectGUIDString();

    char openQuote();

    char closeQuote();

    /**
     * getIdentityInsertString
     *
     * @return String
     */
    String getIdentityInsertString();

    /**
     * getNoColumnsInsertString
     *
     * @return String
     */
    String getNoColumnsInsertString();

    boolean supportsInsertSelectIdentity();


    /**
     * appendIdentitySelectToInsert
     *
     * @param sql String
     * @return String
     */
    String appendIdentitySelectToInsert(String sql);
}
