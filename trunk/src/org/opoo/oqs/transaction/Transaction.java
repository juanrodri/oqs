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
package org.opoo.oqs.transaction;

import javax.transaction.Synchronization;

/**
 * 事务处理接口。
 * <p>编程式事务管理:
 * <pre>
 *
 * Transaction tx = queryFactory.beginTransaction();
 * try
 * {
 *   // execute your business logic here
 *   tx.commit();
 * }
 * catch (Exception ex)
 * {
 *   tx.rollback();
 *   throw ex;
 * }
 * </pre>
 *
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public interface Transaction {
    //void begin() throws TransactionException;

    /**
     * 提交事务。
     * @throws TransactionException
     */
    void commit() throws TransactionException;

    /**
     * 回滚事务。
     * @throws TransactionException
     */
    void rollback() throws TransactionException;

    /**
     * 事务状态是否是已回滚？
     * @return boolean
     * @throws TransactionException
     */
    boolean wasRolledBack() throws TransactionException;

    /**
     * 事务状态是否是已提交？
     * @return boolean
     * @throws TransactionException
     */
    boolean wasCommitted() throws TransactionException;

    /**
     * 事务是否是活动的？
     * @return boolean
     * @throws TransactionException
     */
    boolean isActive() throws TransactionException;

    /**
     * 注册一个Synchronization。
     * @param synchronization Synchronization
     * @throws TransactionException
     */
    void registerSynchronization(Synchronization synchronization) throws
            TransactionException;

}
