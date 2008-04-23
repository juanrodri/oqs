/*
 * $Id: OqsDao.java 1.0 2008Äê4ÔÂ15ÈÕ Alex Lin(alex@opoo.org) $
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
package org.opoo.dao.oqs;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.opoo.dao.Dao;
import org.opoo.dao.Entity;
import org.opoo.dao.support.PageLoader;
import org.opoo.dao.support.PageUtils;
import org.opoo.dao.support.PageableList;
import org.opoo.dao.support.ResultFilter;
import org.opoo.oqs.Mapper;
import org.opoo.oqs.Query;
import org.opoo.oqs.QueryHelper;
import org.opoo.oqs.jdbc.DataAccessException;


/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class OqsDao<T extends Entity<K>, K extends Serializable> extends OqsDaoSupport implements Dao<T, K>, PageLoader{
    protected Class<T> entityClass;
    public OqsDao() {
        super();
    }

    public List<T> find() {
	String sql = "select * from " + getTableName();
	return getQueryFactory().createQuery(sql).setMapper(getMapper()).list();
    }

    public List<T> find(ResultFilter f){
        String sql = "select * from " + getTableName();
        Query q = QueryHelper.createQuery(getQueryFactory(), sql,
                                          f.getCriterion(), f.getOrder());
        q.setMapper(getMapper());
        if (f.isPageable()) {
            q.setFirstResult(f.getFirstResult());
            q.setMaxResults(f.getMaxResults());
        }
        return q.list();
    }

    public PageableList<T> findPageableList(ResultFilter f) {
	return PageUtils.findPageableList(this, f);
    }

    public int getCount(ResultFilter f) {
	String sql = "select count(*) from " + getTableName();
	return getQuerySupport().getInt(sql, f.getCriterion());
    }


    public int remove(K id) throws DataAccessException {
	String sql = "delete from " + getTableName() + " where "
		     + getIdProperty() + "=?";
        return getQueryFactory().createQuery(sql).setParameter(0, id)
		.executeUpdate();
    }

    public int remove(K[] ids) throws DataAccessException {
	if(ids.length == 1){
	    return remove(ids[0]);
	}
	String sql = "delete from " + getTableName() + " where "
		     + getIdProperty() + " in (:ids)";
	return getQueryFactory().createQuery(sql).setParameterList("ids", ids)
		.executeUpdate();
    }

    public T get(K id) throws DataAccessException {
	String sql = "select * from " + getTableName() + " where "
		     + getIdProperty() + "=?";
        return (T)getQueryFactory().createQuery(sql)
		.setParameter(0, id)
		.setMapper(getMapper())
		.uniqueResult();
    }

    public int getCount() throws DataAccessException {
        return getCount(ResultFilter.EMPTY_FILTER);
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
    protected String getTableName(){
	return getEntityClass().getSimpleName();
    };
    protected String getClassName(){
	return getEntityClass().getName();
    }
    protected String getIdProperty(){
	return "id";
    }
    protected abstract Mapper getMapper();
}
