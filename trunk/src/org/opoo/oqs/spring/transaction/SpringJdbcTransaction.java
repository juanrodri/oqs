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
package org.opoo.oqs.spring.transaction;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.transaction.AbstractJdbcTransaction;
import org.opoo.oqs.transaction.Transaction;
import org.opoo.oqs.transaction.TransactionException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 *<tt>Transaction</tt>的实现类，
 * {@link org.opoo.oqs.spring.SpringQueryFactoryImpl}中采用了
 * 这种事务处理。当底层数据访问采用Spring的JDBC操作实现时，应该使用此类类管理事务。
 *

 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class SpringJdbcTransaction extends AbstractJdbcTransaction implements
        Transaction {
    private static final Log log = LogFactory.getLog(SpringJdbcTransaction.class);
    private DataSourceTransactionManager transactionManager;
    private TransactionStatus status;

    public SpringJdbcTransaction(DataSource ds) {
        transactionManager = new DataSourceTransactionManager(ds);
    }


    public void begin() throws TransactionException {
        log.debug("begin");

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        status = transactionManager.getTransaction(def);
        begun = true;
    }


    public void commit() throws TransactionException {
        if (!begun) {
            throw new TransactionException(
                    "Transaction not successfully started");
        }

        log.debug("commit");

        try {
            transactionManager.commit(status);
            committed = true;
        } catch (Exception ex) {
            commitFailed = true;
        }
    }


    public void rollback() throws TransactionException {
        if (!begun) {
            throw new TransactionException(
                    "Transaction not successfully started");
        }

        log.debug("rollback");

        transactionManager.rollback(status);
        rolledBack = true;
    }
}
