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
 * <tt>double</tt>: A type that maps an SQL DOUBLE to a Java Double.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class DoubleType extends PrimitiveType {


    public Object valueOf(String value) {
        return new Double(value);
    }


    public Object get(ResultSet rs, String name) throws SQLException {
        return new Double(rs.getDouble(name));
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        return new Double(rs.getDouble(index));
    }

    public Serializable getDefaultValue() {
        return new Double(0.0);
    }


    public Class getPrimitiveClass() {
        return double.class;
    }


    public Class getReturnedClass() {
        return Double.class;
    }

    public String toSQLString(Object value) throws Exception {
        return value.toString();
    }


    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        st.setDouble(index, ((Double) value).doubleValue());
    }


    public int sqlType() {
        return Types.DOUBLE;
    }
}
