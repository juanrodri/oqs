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

import java.util.List;

import org.opoo.oqs.criterion.Criterion;
import org.opoo.oqs.criterion.Order;

/**
 *
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public interface Criteria {
    Criteria add(Criterion criterion);

    Criteria addOrder(Order order);

    Object uniqueResult();

    List list();

    QueryIterator iterate();

    Criteria setMaxResults(int maxResults);

    Criteria setFirstResult(int firstResult);

    Criteria setFetchSize(int fetchSize);

    Criteria setTimeout(int timeout);

    Criteria setMapper(Mapper mapper);
}
