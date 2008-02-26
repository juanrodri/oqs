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
package org.opoo.oqs.support;

import java.util.List;

import org.opoo.oqs.Query;
import org.opoo.oqs.QueryAware;
import org.opoo.oqs.QueryFactory;
import org.opoo.oqs.QueryHelper;
import org.opoo.oqs.criterion.Criterion;
import org.opoo.util.Assert;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
public class OqsQuerySupport implements QuerySupport, QueryAware{
    private QueryFactory qf;
    public OqsQuerySupport(){
    }
    public OqsQuerySupport(QueryFactory qf) {
        this.qf = qf;
    }
    /**
     *
     * @param baseSql String
     * @param c Criterion
     * @return int
     */
    public int executeUpdate(String baseSql, Criterion c) {
        return QueryHelper.createQuery(qf, baseSql, c).executeUpdate();
    }

    /**
     *
     * @param baseSql String
     * @param resultFilter ResultFilter
     * @return List
     */
    public List find(String baseSql, ResultFilter resultFilter) {
        Query q = QueryHelper.createQuery(qf, baseSql,
                                          resultFilter.getCriterion(),
                                          resultFilter.getOrder());
        if (resultFilter.isPageable()) {
            q.setFirstResult(resultFilter.getFirstResult());
            q.setMaxResults(resultFilter.getMaxResults());
        }
        return q.list();
    }

    /**
     *
     * @param baseSelectSql String
     * @param baseCountSql String
     * @param resultFilter ResultFilter
     * @return PagedList
     */
    public PageableList find(String baseSelectSql, String baseCountSql,
                          ResultFilter resultFilter) {
        Assert.isTrue(resultFilter.isPageable());
        List list = find(baseSelectSql, resultFilter);
        int count = getInt(baseCountSql, resultFilter.getCriterion());
        return new PageableList(list, resultFilter.getFirstResult(),
                             resultFilter.getMaxResults(), count);
    }

    /**
     *
     * @param baseSql String
     * @param c Criterion
     * @return int
     */
    public int getInt(String baseSql, Criterion c) {
        Query q = QueryHelper.createQuery(qf, baseSql, c);
        Number number = (Number) q.uniqueResult();
        return number.intValue();
    }

    public void setQueryFactory(QueryFactory qf) {
	this.qf = qf;
    }
    public QueryFactory getQueryFactory(){
	return qf;
    }
}
