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
package org.opoo.dao.ibatis;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.opoo.dao.Dao;
import org.opoo.dao.Entity;
import org.opoo.dao.support.PageLoader;
import org.opoo.dao.support.PageUtils;
import org.opoo.dao.support.PageableList;
import org.opoo.dao.support.ResultFilter;
import org.opoo.oqs.jdbc.DataAccessException;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;


public abstract class SqlMapDao<T extends Entity<K>, K extends Serializable> extends SqlMapClientDaoSupport implements Dao<T, K>, PageLoader {
    public SqlMapDao() {
        super();
    }

    public T save(T entity) throws DataAccessException {
	getSqlMapClientTemplate().insert("insert", entity);
        return entity;
    }

    public T update(T entity) throws DataAccessException {
	getSqlMapClientTemplate().update("update", entity);
        return entity;
    }

    public T saveOrUpdate(T entity) throws DataAccessException {
	throw new UnsupportedOperationException("saveOrUpdate");
    }

    public int delete(T entity) throws DataAccessException {
	getSqlMapClientTemplate().delete("delete", entity);
        return 1;
    }

    public int remove(K id) throws DataAccessException {
        try {
            T t = newEntity();
	    t.setId(id);
	    delete(t);
        } catch (Exception ex) {
	    throw new DataAccessException(ex);
	}
        return 1;
    }

    public int remove(K[] ids) throws DataAccessException {
	for(int i = 0 ; i < ids.length ; i++){
	    remove(ids[i]);
	}
        return ids.length;
    }

    public T get(K id) throws DataAccessException {
	return (T)getSqlMapClientTemplate().queryForObject("get", id);
    }

    public List<T> find() throws DataAccessException {
        return getSqlMapClientTemplate().queryForList("list", null);
    }

    public List<T> find(ResultFilter resultFilter) throws DataAccessException {
        return getSqlMapClientTemplate().queryForList("list",
                resultFilter.getFirstResult(),
                resultFilter.getMaxResults());
    }

    public int getCount() throws DataAccessException {
	Number n = (Number) getSqlMapClientTemplate().queryForObject("count", null);
        return n.intValue();
    }

    public int getCount(ResultFilter resultFilter) throws DataAccessException {
        return getCount();
    }

    public PageableList findPageableList(ResultFilter resultFilter) throws
            DataAccessException {
        return PageUtils.findPageableList(this, resultFilter);
    }

    protected Class<T> entityClass;
    protected Class<T> getEntityClass() {
        if (entityClass == null) {
            entityClass = (Class<T>) ((ParameterizedType) getClass()
                                      .getGenericSuperclass()).
                    getActualTypeArguments()[0];
            logger.debug("T class = " + entityClass.getName());
        }
        return entityClass;
    }
    protected T newEntity() throws IllegalAccessException,
            InstantiationException {
	Class<T> entityClass = getEntityClass();
	return entityClass.newInstance();
    }
}
