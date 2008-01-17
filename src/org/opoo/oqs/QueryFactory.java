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
package org.opoo.oqs;

import org.opoo.oqs.jdbc.ConnectionManager;
import org.opoo.oqs.transaction.Transaction;
import org.opoo.oqs.transaction.TransactionException;


/**
 * 描述创建Query对象的工厂类的接口。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since OQS1.0
 */
public interface QueryFactory {
    /**
     * 创建一个Query实例。
     *
     * @param queryString String
     * @return Query
     * @throws CannotCreateQueryException
     */
    Query createQuery(String queryString) throws CannotCreateQueryException;

    /**
     *
     * @param queryString String
     * @return Criteria
     * @throws CannotCreateQueryException
     */
    Criteria createCriteria(String queryString) throws
            CannotCreateQueryException;

    /**
     *
     * @return StatementBatcher
     */
    StatementBatcher createBatcher();

    /**
     *
     * @param sql String
     * @return PreparedStatementBatcher
     */
    PreparedStatementBatcher createBatcher(String sql);

    /**
     * 取得当前QueryFactory所使用的ConnectionManager对象。
     *
     * @return ConnectionManager
     */
    ConnectionManager getConnectionManager();

    /**
     * 取得当前QueryFactory所使用的Dialect对象。
     *
     * @return Dialect
     */
    //Dialect getDialect();

    /**
     * 取得当前数据源的Transaction并开始事务处理。
     * @return Transaction
     * @throws TransactionException
     */
    Transaction beginTransaction() throws TransactionException;

}
