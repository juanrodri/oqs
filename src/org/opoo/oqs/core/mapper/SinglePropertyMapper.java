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
package org.opoo.oqs.core.mapper;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.core.SingleProperty;
import org.opoo.oqs.type.Type;
import org.opoo.oqs.type.TypeFactory;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class SinglePropertyMapper extends SingleProperty implements
        PropertyMapper {
    private static final Log log = LogFactory.getLog(SinglePropertyMapper.class);
    ResultSetMetaData rsmd = null;
    public SinglePropertyMapper(String name, String string, int index) {
        super(name, string, index);
    }

    public SinglePropertyMapper(SingleProperty sp) {
        super(sp.getName(), sp.getString(), sp.getIndex());
    }


    public Object map(ResultSet rs, int rowNum) throws SQLException {
        if (getIndex() > 0) {
            log.debug("get by index: " + getIndex());
            return getColumnValue(rs, getIndex());
        } else {
            log.debug("get by name: " + getName());
            return getColumnValue(rs, getName());
        }
    }

    protected Object getColumnValue(ResultSet rs, int index) throws
            SQLException {
        Type type = getType();
        if (type == null && rsmd != null) {
            type = guessType(rsmd, index);
        }
        if (type != null) {
            return type.safeGet(rs, index);
        }
        return getResultSetValue(rs, index);
    }

    protected Type guessType(ResultSetMetaData rsmd, int i) {
        try {
            String className = rsmd.getColumnClassName(i);
            return TypeFactory.guessType(className);
        } catch (SQLException ex) {
            log.error(
                    "cannot guess type from result metadata column classname: " +
                    i, ex);
        }
        return null;
    }

    protected Object getColumnValue(ResultSet rs, String name) throws
            SQLException {
        Type type = getType();
        if (type == null && rsmd != null) {
            type = guessType(rsmd, name);
        }
        if (type != null) {
            return type.safeGet(rs, name);
        }
        return rs.getObject(name);
    }

    protected Type guessType(ResultSetMetaData rsmd, String name) {
        try {
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                if (name.equals(rsmd.getColumnName(i))) {
                    String className = rsmd.getColumnClassName(i);
                    return TypeFactory.guessType(className);
                }
            }
        } catch (SQLException ex) {
            log.error(
                    "cannot guess type from result metadata column classname:" +
                    name, ex);
        }
        return null;
    }

    public static Object getResultSetValue(ResultSet rs, int index) throws
            SQLException {
        Object obj = rs.getObject(index);
        if (obj instanceof Blob) {
            obj = rs.getBytes(index);
        } else if (obj instanceof Clob) {
            obj = rs.getString(index);
        } else if (obj != null &&
                   obj.getClass().getName().startsWith("oracle.sql.TIMESTAMP")) {
            obj = rs.getTimestamp(index);
        } else if (obj != null &&
                   obj.getClass().getName().startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(
                    index);
            if ("java.sql.Timestamp".equals(metaDataClassName) ||
                "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if (obj != null && obj instanceof java.sql.Date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(
                    index))) {
                obj = rs.getTimestamp(index);
            }
        }
        return obj;
    }


    public void initialize(ResultSetMetaData rsmd) throws SQLException {
        log.debug(getClass().getName() + ".initialize() is called.");
        this.rsmd = rsmd;
    }

    public String getMapperString() {
        return "[SinglePropertyMapper{name=" + getName() + ", string="
                + getString() + ", index=" + getIndex() + "}]";
    }

    public Class getReturnType() {
        return null;
    }
}
