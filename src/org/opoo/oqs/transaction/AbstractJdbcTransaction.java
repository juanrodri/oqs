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

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Synchronization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JDBC事务处理的抽象实现。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class AbstractJdbcTransaction implements Transaction {
    private static final Log log = LogFactory.getLog(AbstractJdbcTransaction.class);
    protected boolean rolledBack;
    protected boolean committed;
    protected boolean begun;
    protected boolean commitFailed;
    protected List synchronizations;

    /**
     * 开始一个事务。
     * @throws TransactionException
     */
    protected abstract void begin() throws TransactionException;


    public boolean isActive() throws TransactionException {
        return begun && !(rolledBack || committed | commitFailed);
    }

    public void registerSynchronization(Synchronization sync) throws
            TransactionException {
        if (sync == null) {
            throw new NullPointerException("null Synchronization");
        }
        if (synchronizations == null) {
            synchronizations = new ArrayList();
        }
        synchronizations.add(sync);
    }

    public boolean wasCommitted() throws TransactionException {
        return committed;
    }

    public boolean wasRolledBack() throws TransactionException {
        return rolledBack;
    }
}
