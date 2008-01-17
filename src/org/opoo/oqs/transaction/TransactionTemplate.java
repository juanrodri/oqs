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

import org.opoo.oqs.QueryAware;
import org.opoo.oqs.QueryFactory;


/**
 * Helper class that simplifies programmatic transaction demarcation
 * and transaction exception handling.
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @see Transaction
 */
public class TransactionTemplate implements QueryAware {
    private Transaction transaction;
    private QueryFactory queryFactory;
    public TransactionTemplate() {
    }

    public TransactionTemplate(QueryFactory qf) {
        setQueryFactory(qf);
        transaction = qf.beginTransaction();
        if (transaction == null) {
            throw new IllegalArgumentException("transaction is required");
        }
    }

    /*
       public void setTransaction(Transaction transaction)
       {
      this.transaction = transaction;
       }*/

    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Execute the action specified by the given callback object within a transaction.
     * <p>Allows for returning a result object created within the transaction, i.e.
     * a domain object or a collection of domain objects. A RuntimeException thrown
     * by the callback is treated as application exception that enforces a rollback.
     *
     * @param callback TransactionCallback
     * @return Object
     * @throws TransactionException
     */
    public final Object execute(TransactionCallback callback) throws
            TransactionException {
        Object result = null;

        try {
            result = callback.doInTransaction(getQueryFactory());
            transaction.commit();
        } catch (RuntimeException ex) {
            // transactional code threw application exception -> rollback
            transaction.rollback();
            throw ex;
        } catch (Error err) {
            // transactional code threw error -> rollback
            transaction.rollback();
            throw err;
        }
        return result;
    }

    public void setQueryFactory(QueryFactory qf) {
        this.queryFactory = qf;
    }

    public QueryFactory getQueryFactory() {
        return queryFactory;
    }
}
