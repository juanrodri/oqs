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
package org.opoo.oqs.transaction;

import org.opoo.oqs.QueryFactory;


/**
 * Callback interface for transactional code. To be used with TransactionTemplate's
 * execute method, assumably often as anonymous class within a method implementation.
 *
 *
 * @author Juergen Hoeller
 * @author Alex Lin(alex@opoo.org)
 * @see TransactionTemplate
 */
public interface TransactionCallback {

    /**
     * Gets called by TransactionTemplate.execute within a transactional context.
     *
     * <p>Allows for returning a result object created within the transaction, i.e.
     * a domain object or a collection of domain objects. A RuntimeException thrown
     * by the callback is treated as application exception that enforces a rollback.
     * An exception gets propagated to the caller of the template.
     *
     * <p>Note when using JTA: JTA transactions only work with transactional
     * JNDI resources, so implementations need to use such resources if they
     * want transaction support.
     *
     *
     * @param qf QueryFactory
     * @return Object
     */
    Object doInTransaction(QueryFactory qf);
}
