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
package org.opoo.dao;

import java.io.Serializable;
import java.util.List;

import org.opoo.dao.support.PageableList;
import org.opoo.dao.support.ResultFilter;
import org.opoo.oqs.criterion.Criterion;
import org.opoo.oqs.jdbc.DataAccessException;

/**
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public interface Dao<T extends Entity<K>, K extends Serializable> {
    T save(T entity) throws DataAccessException;
    T update(T entity) throws DataAccessException;
    T saveOrUpdate(T entity) throws DataAccessException;
    int delete(T entity) throws DataAccessException;
    int remove(K id) throws DataAccessException;
    int remove(K[] ids) throws DataAccessException;
    int remove(Criterion criterion) throws DataAccessException;
    T get(K id) throws DataAccessException;

    List<T> find() throws DataAccessException;
    List<T> find(ResultFilter resultFilter) throws DataAccessException;
    int getCount() throws DataAccessException;
    int getCount(ResultFilter resultFilter) throws DataAccessException;
    PageableList<T> findPageableList(ResultFilter resultFilter) throws DataAccessException;
}
