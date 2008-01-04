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
package org.opoo.oqs.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * <tt>boolean</tt>: A type that maps an SQL BIT to a Java Boolean.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class BooleanType extends PrimitiveType {
    private static final String TRUE = "1";
    private static final String FALSE = "0";

    public Object valueOf(String value) {
        return Boolean.valueOf(value);
    }

    public Object get(ResultSet rs, String name) throws SQLException {
        return rs.getBoolean(name) ? Boolean.TRUE : Boolean.FALSE;
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        return rs.getBoolean(index) ? Boolean.TRUE : Boolean.FALSE;
    }

    public Serializable getDefaultValue() {
        return Boolean.FALSE;
    }

    public Class getPrimitiveClass() {
        return boolean.class;
    }

    public Class getReturnedClass() {
        return Boolean.class;
    }

    public String toSQLString(Object value) throws Exception {
        return (((Boolean) value).booleanValue()) ? TRUE : FALSE;
    }

    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        st.setBoolean(index, ((Boolean) value).booleanValue());
    }

    public int sqlType() {
        return Types.BIT;
    }
}
