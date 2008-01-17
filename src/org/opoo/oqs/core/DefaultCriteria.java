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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.Criteria;
import org.opoo.oqs.Mapper;
import org.opoo.oqs.Query;
import org.opoo.oqs.QueryHelper;
import org.opoo.oqs.QueryIterator;
import org.opoo.oqs.criterion.Criterion;
import org.opoo.oqs.criterion.Logic;
import org.opoo.oqs.criterion.Order;
import org.opoo.oqs.criterion.Restrictions;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class DefaultCriteria implements Criteria {
    private static final Log log = LogFactory.getLog(DefaultCriteria.class);
    private final AbstractQueryFactory queryFactory;
    private String queryString;
    private Logic criterions = null;
    private Order order = null;
    private Mapper mapper;
    private int firstResult = 0;
    private int maxResults = -1;
    private int fetchSize = 0;
    private int timeout = 0;

    public DefaultCriteria(AbstractQueryFactory queryFactory,
                           String queryString) {

        this.queryFactory = queryFactory;
        this.queryString = queryString;
        //query = (AbstractQuery) queryFactory.createQuery(queryString);
    }

    /**
     * add
     *
     * @param criterion Criterion
     * @return Criteria
     */
    public Criteria add(Criterion criterion) {
        if (criterions == null) {
            criterions = Restrictions.logic(criterion);
        } else {
            criterions.and(criterion);
        }
        return this;
    }

    /**
     * addOrder
     *
     * @param order Order
     * @return Criteria
     */
    public Criteria addOrder(Order order) {
        if (this.order == null) {
            this.order = order;
        } else {
            this.order.add(order);
        }
        return this;
    }

    /**
     * iterate
     *
     * @return QueryIterator
     */
    public QueryIterator iterate() {
        return createQuery().iterate();
    }

    /**
     * list
     *
     * @return List
     */
    public List list() {
        return createQuery().list();
    }


    /**
     * setFetchSize
     *
     * @param fetchSize int
     * @return Criteria
     */
    public Criteria setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    /**
     * setFirstResult
     *
     * @param firstResult int
     * @return Criteria
     */
    public Criteria setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    /**
     * setMaxResults
     *
     * @param maxResults int
     * @return Criteria

     */
    public Criteria setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }


    /**
     * setTimeout
     *
     * @param timeout int
     * @return Criteria
     */
    public Criteria setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * uniqueResult
     *
     * @return Object
     */
    public Object uniqueResult() {
        return createQuery().uniqueResult();
    }

    Query createQuery() {
        Query q = QueryHelper.createQuery(queryFactory, queryString, criterions,
                                          order);
        if (mapper != null) {
            q.setMapper(mapper);
        }
        if (firstResult > 0) {
            q.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            q.setMaxResults(maxResults);
        }
        if (fetchSize > 0) {
            q.setFetchSize(fetchSize);
        }
        if (timeout > 0) {
            q.setQueryTimeout(timeout);
        }
        return q;
    }

    public Criteria setMapper(Mapper mapper) {
        this.mapper = mapper;
        return this;
    }
}
