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
package org.opoo.dao.hibernate3;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;
import org.opoo.oqs.QueryHelper;
import org.opoo.oqs.criterion.Criterion;
import org.opoo.oqs.criterion.Order;
import org.opoo.oqs.type.Type;
import org.opoo.util.ArrayUtils;
import org.opoo.util.Assert;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class HibernateQueryHelper {

    public static Query createQuery(Session session,
						  String baseSql,
						  Criterion c, Order o) {
	String qs = QueryHelper.buildQueryString(baseSql, c, o);
	org.hibernate.Query q = session.createQuery(qs);
	if(c != null){
            Object[] values = c.getValues();
            //Type[] types = c.getTypes();
            if (!ArrayUtils.isEmpty(values)) {
                //q.setParameters(values, types);
                for (int i = 0; i < values.length; i++) {
                    //q.setParameter(i, values[i], fromOqsType(types[i]));
                    q.setParameter(i, values[i]);
                }
            }
        }
	return q;
    }

    public static Query createQuery(Session session,
						  String baseSql,
						  Criterion c){
	return createQuery(session, baseSql, c, null);
    }

    public static Query createQuery(Session session,
                                    String sql,
				    String[] names,
                                    Object[] values) {

	Query q = session.createQuery(sql);
	if(names != null){
	    Assert.isTrue(names.length == values.length);
            for (int i = 0 ; i < names.length; i++) {
                Object obj = values[i];
                if (obj.getClass().isArray()) {
                    q.setParameterList(names[i], (Object[]) obj);
                } else if (obj instanceof Collection) {
                    q.setParameterList(names[i], (Collection) obj);
                } else {
                    q.setParameter(names[i], obj);
                }
            }
	}
	return q;
    }

    public static Query createQuery(Session session, String sql, String name,
                                    Object value) {
        return createQuery(session, sql, new String[] {name},
                           new Object[] {value});
    }
    public static Query createQuery(Session session, String sql, String name,
				    Object[] values){
	Query q = session.createQuery(sql);
	q.setParameterList(name, values);
        return q;
    }

    public static Query createQuery(Session session, String sql) {
        return session.createQuery(sql);
    }

    public static Query createQuery(Session session, String sql, Object value) {
        return createQuery(session, sql, new Object[] {value});
    }

    public static Query createQuery(Session session, String sql,
                                    Object[] values) {
        Query q = session.createQuery(sql);
        for (int i = 0; i < values.length; i++) {
            q.setParameter(i, values[i]);
        }
        return q;
    }


    public static org.hibernate.type.Type fromOqsType(Type t){
	Class clazz = t.getReturnedClass();
	return org.hibernate.type.TypeFactory.heuristicType(clazz.getName());
    }
}
