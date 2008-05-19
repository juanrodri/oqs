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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.opoo.dao.Dao;
import org.opoo.dao.Entity;
import org.opoo.dao.support.PageLoader;
import org.opoo.dao.support.PageUtils;
import org.opoo.dao.support.PageableList;
import org.opoo.dao.support.ResultFilter;
import org.opoo.oqs.jdbc.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.opoo.oqs.criterion.*;
import org.hibernate.*;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class HibernateDao<T extends Entity<K>, K extends Serializable> extends HibernateDaoSupport implements Dao<T, K>, PageLoader{
    protected Class<T> entityClass;
    public HibernateDao() {
        super();
    }

    public T save(T entity) throws DataAccessException {
	getHibernateTemplate().save(entity);
	return entity;
    }

    public T update(T entity) throws DataAccessException {
	getHibernateTemplate().update(entity);
        return entity;
    }

    public T saveOrUpdate(T entity) throws DataAccessException {
        getHibernateTemplate().saveOrUpdate(entity);
	return entity;
    }

    public int delete(T entity) throws DataAccessException {
	getHibernateTemplate().delete(entity);
        return 1;
    }

    public int remove(final K id) throws DataAccessException {
        final String sql = "delete from " + getEntityName() + " where "
                           + getIdProperty() + "=?";
        return ((Integer)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws SQLException,
                    HibernateException {
                return Integer.valueOf(session.createQuery(sql).setParameter(0,
                        id).executeUpdate());
            }
        })).intValue();
    }

    public int remove(K[] ids) throws DataAccessException {
	if(ids.length == 1){
	    remove(ids[0]);
	}
	String sql = "delete from " + getEntityName() + " where "
		     + getIdProperty() + " in (:ids)";
        return getQuerySupport().executeUpdate(sql, "ids", ids);
    }

    public int remove(final Criterion c) throws DataAccessException {
        String sql = "delete from " + getEntityName();
        if (c != null) {
	    return getQuerySupport().executeUpdate(sql, c);
        } else {
            return getQuerySupport().executeUpdate(sql);
        }
    }

    public T get(K id) throws DataAccessException {
        return (T)getHibernateTemplate().get(getEntityClass(), id);
    }

    public List<T> find() throws DataAccessException {
        return getHibernateTemplate().loadAll(getEntityClass());
    }

    public List<T> find(ResultFilter resultFilter) throws DataAccessException {
        return getQuerySupport().find("from " + getEntityName(), resultFilter);
    }

    public PageableList<T> findPageableList(ResultFilter resultFilter) throws DataAccessException {
	return PageUtils.findPageableList(this, resultFilter);
    }

    public int getCount() throws DataAccessException {
        return getCount(ResultFilter.createEmptyResultFilter());
    }

    public int getCount(ResultFilter resultFilter) throws DataAccessException {
        return getQuerySupport().getInt("select count(*) from " + getEntityName(), resultFilter.getCriterion());
    }


    protected String getEntityName() {
	return getEntityClass().getName();
    }
    protected void setEntityClass(Class<T> clazz){
	entityClass = clazz;
    }
    protected Class<T> getEntityClass() {
        if (entityClass == null) {
            entityClass = (Class<T>) ((ParameterizedType) getClass()
                                      .getGenericSuperclass()).
                    getActualTypeArguments()[0];
            logger.debug("T class = " + entityClass.getName());
        }
        return entityClass;
    }

    protected String getIdProperty() {
        return "id";
    }
}
