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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.opoo.oqs.Mapper;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class MapperAdaptor implements Mapper {
    public MapperAdaptor() {
    }

    /**
     * 在循环之外调用，只能被调用一次
     *
     * @param rsmd ResultSetMetaData
     * @throws SQLException
     */
    public void initialize(ResultSetMetaData rsmd) throws SQLException {
    }

    /**
     * 循环中调用，映射每一个属性或者每一行
     *
     * @param rs ResultSet
     * @param rowNum int
     * @return Object
     * @throws SQLException
     */
    public Object map(ResultSet rs, int rowNum) throws SQLException {
	ResultSetMetaData meta = rs.getMetaData();
	int count = meta.getColumnCount();
	Object[] objects = new Object[count];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = rs.getObject(i + 1);
        }
        return objects;
    }
}
