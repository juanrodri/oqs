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

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * <tt>clob</tt>: A type that maps an SQL CLOB to a java.sql.Clob.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class ClobType implements Type {

    public Object valueOf(String value) {
        throw new UnsupportedOperationException("todo");
    }


    public Class getReturnedClass() {
        return Clob.class;
    }


    public boolean isMutable() {
        return false;
    }


    public Object safeGet(ResultSet rs, String name) throws SQLException {
        Clob value = rs.getClob(name);
        return value;
    }

    /**
     *
     * @param rs ResultSet
     * @param index int
     * @return Object
     * @throws SQLException
     */
    public Object safeGet(ResultSet rs, int index) throws SQLException {
        Clob value = rs.getClob(index);
        return value;
    }

    public void safeSet(PreparedStatement st, Object value, int index) throws
            SQLException {
        if (value == null) {
            st.setNull(index, Types.CLOB);
        } else {
            st.setClob(index, (Clob) value);
        }
    }

    public int sqlType() {
        return Types.CLOB;
    }
}
