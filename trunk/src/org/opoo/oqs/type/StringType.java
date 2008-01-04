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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * <tt>string</tt>: A type that maps an SQL VARCHAR to a Java String.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class StringType extends ImmutableType implements LiteralType {


    public Object valueOf(String value) {
        return value;
    }

    public Object get(ResultSet rs, String name) throws SQLException {
        return rs.getString(name);
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        st.setString(index, (String) value);
    }


    public int sqlType() {
        return Types.VARCHAR;
    }


    public String toString(Object value) {
        return (String) value;
    }

    public String toSQLString(Object value) throws Exception {
        return '\'' + (String) value + '\'';
    }

    public Class getReturnedClass() {
        return String.class;
    }
}
