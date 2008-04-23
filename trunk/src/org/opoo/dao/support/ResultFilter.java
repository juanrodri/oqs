/*
 * $Id: ResultFilter.java 54 2008-02-26 08:08:31Z alex@opoo.org $
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
package org.opoo.dao.support;

import java.io.Serializable;

import org.opoo.oqs.criterion.Criterion;
import org.opoo.oqs.criterion.Order;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
public class ResultFilter {
    public static final ResultFilter EMPTY_FILTER = new ResultFilter();
    public static final int INVALID_INT = -1;
    public static int MAX_RESULTS = 25;
    private Criterion criterion;
    private Order order;
    private int firstResult = INVALID_INT;
    private int maxResults = MAX_RESULTS;

    public ResultFilter() {
    }
    public ResultFilter(Criterion c, Order order){
	this.criterion = c;
	this.order = order;
    }
    public ResultFilter(Criterion c, Order o, int firstResult, int maxResults){
	this(c, o);
	setFirstResult(firstResult);
	setMaxResults(maxResults);
    }


    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public Order getOrder() {
        return order;
    }


    public boolean isPageable() {
        return firstResult != INVALID_INT;
    }


    public int getMaxResults() {
        return maxResults;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public String toString() {
        /*return "{pi:" + pageInfo.toString()
            + ",c:" + criterion.toString() +
            ",o:" + sortSql.toString() + "}";  */
        StringBuffer sb = new StringBuffer("rf{p:")
                          .append(firstResult).append(",").append(maxResults);
        if (criterion != null) {
            sb.append(criterion.toString());
        }
        if (order != null) {
            sb.append(order.toString());
        }
        return sb.toString();
    }

    public Serializable toKey() {
        String str = toString();
        Object[] vs = criterion.getValues();
        if (vs != null) {
            StringBuffer sb = new StringBuffer(str);
            for (int j = 0; j < vs.length; j++) {
                sb.append(".").append(vs[j]);
            }
            return sb.toString();
        }
        return str;
    }

    public static ResultFilter createEmptyResultFilter() {
        return new ResultFilter();
    }

    public static ResultFilter createPageableResultFilter(int firstResult, int maxResults){
	return new ResultFilter(null, null, firstResult, maxResults);
    }
}
