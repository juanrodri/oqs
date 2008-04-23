/*
 * $Id: HibernateQuerySupport.java 1.0 2008年2月25日 下午05时01分12秒 $
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
package org.opoo.dao.hibernate3;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.opoo.dao.support.PageableList;
import org.opoo.dao.support.QuerySupport;
import org.opoo.dao.support.ResultFilter;
import org.opoo.oqs.criterion.Criterion;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class HibernateQuerySupport implements QuerySupport {
    private HibernateTemplate template;
    public HibernateQuerySupport(HibernateTemplate template) {
        this.template = template;
    }

    public int executeUpdate(final String baseSql, final Criterion c) {
        return ((Number)template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws SQLException,
                    HibernateException {
                return Integer.valueOf(HibernateQueryHelper.createQuery(session,
                        baseSql, c).executeUpdate());
            }
        })).intValue();
    }

    public List find(final String baseSql, final ResultFilter resultFilter) {
        return template.executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws SQLException,
                    HibernateException {
                Query q = HibernateQueryHelper.createQuery(session, baseSql,
                        resultFilter.getCriterion(),
                        resultFilter.getOrder());
                if (resultFilter.isPageable()) {
                    q.setFirstResult(resultFilter.getFirstResult());
                    q.setMaxResults(resultFilter.getMaxResults());
                }
                return q.list();
            }
        });
    }

    public PageableList find(String baseSelectSql, String baseCountSql,
                          ResultFilter resultFilter) {
        List l = find(baseSelectSql, resultFilter);
        int count = getInt(baseCountSql, resultFilter.getCriterion());
        return new PageableList(l, resultFilter.getFirstResult(),
                             resultFilter.getMaxResults(), count);
    }

    public int getInt(final String baseSql, final Criterion c) {
        return ((Number) template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws SQLException,
                    HibernateException {
                return (Number) HibernateQueryHelper.createQuery(session,
                        baseSql,
                        c).uniqueResult();
            }
        })).intValue();
    }

    public List find(String queryString) {
	return template.find(queryString);
    }
    public List find(String queryString, Object value){
	return template.find(queryString, value);
    }
    public List find(String queryString, Object[] values){
	return template.find(queryString, values);
    }

    public int executeUpdate(final String queryString){
        return ((Integer) template.execute(new HibernateCallback() {
            public Object doInHibernate(Session s) throws HibernateException,
                    SQLException {
                int r2 = s.createQuery(queryString).executeUpdate();
                return new Integer(r2);
            }
        })).intValue();
    }

    public int executeUpdate(final String queryString, final Object[] values) {
        return ( (Integer) template.execute(new HibernateCallback() {
            public Object doInHibernate(Session s) throws HibernateException,
                    SQLException {
                int r2 = HibernateQueryHelper.createQuery(s, queryString,
                        values).executeUpdate();
                return new Integer(r2);
            }
        })).intValue();
    }

    public int executeUpdate(final String queryString, final Object value) {
	return ((Integer) template.execute(new HibernateCallback() {
	    public Object doInHibernate(Session s) throws HibernateException,
		    SQLException {
		int r2 = HibernateQueryHelper.createQuery(s, queryString,
			value).executeUpdate();
		return new Integer(r2);
	    }
	})).intValue();
    }


    public List find(final String queryString, final String name,
                     final Object value) {
        return template.executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws SQLException,
                    HibernateException {
                return HibernateQueryHelper.createQuery(session, queryString,
                        name, value).list();
            }
        });
    }

    public List find(final String queryString, final String name,
		     final Object[] values) {
	return template.executeFind(new HibernateCallback() {
	    public Object doInHibernate(Session session) throws SQLException,
		    HibernateException {
		return HibernateQueryHelper.createQuery(session, queryString,
			name, values).list();
	    }
	});
    }

    public List find(final String queryString, final String[] names,
                     final Object[] values) {
        return template.executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws SQLException,
                    HibernateException {
                return HibernateQueryHelper.createQuery(session, queryString,
                        names, values).list();
            }
        });
    }

    public int executeUpdate(final String queryString, final String[] names,
                             final Object[] values) {
        return ((Number) template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws SQLException,
                    HibernateException {
                return new Integer(HibernateQueryHelper.createQuery(session,
                        queryString, names, values).executeUpdate());
            }
        })).intValue();
    }

    public int executeUpdate(final String queryString, final String name,
                             final Object[] values) {
        return ((Number) template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws SQLException,
                    HibernateException {
                return new Integer(HibernateQueryHelper.createQuery(session,
                        queryString, name, values).executeUpdate());
            }
        })).intValue();
    }

    public int executeUpdate(final String queryString, final String name,
                             final Object value) {
        return ((Number) template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws SQLException,
                    HibernateException {
                return new Integer(HibernateQueryHelper.createQuery(session,
                        queryString, name, value).executeUpdate());
            }
        })).intValue();
    }
}
