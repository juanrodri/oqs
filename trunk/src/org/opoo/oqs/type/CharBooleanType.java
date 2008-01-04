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
 * Superclass for types that map Java boolean to SQL CHAR(1).
 *
 * @author Alex Lin
 * @version 1.0
 */
public abstract class CharBooleanType extends BooleanType {
    protected abstract String getTrueString();

    protected abstract String getFalseString();

    public Object get(ResultSet rs, String name) throws SQLException {
        String code = rs.getString(name);
        if (code == null || code.length() == 0) {
            return null;
        } else {
            return getTrueString().equalsIgnoreCase(code.trim()) ?
                    Boolean.TRUE : Boolean.FALSE;
        }
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        String code = rs.getString(index);
        if (code == null || code.length() == 0) {
            return null;
        } else {
            return getTrueString().equalsIgnoreCase(code.trim()) ?
                    Boolean.TRUE : Boolean.FALSE;
        }
    }


    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        st.setString(index, toCharacter(value));

    }

    public int sqlType() {
        return Types.CHAR;
    }

    private String toCharacter(Object value) {
        return ((Boolean) value).booleanValue() ? getTrueString() :
                getFalseString();
    }

    public String toSQLString(Object value) throws Exception {
        return "'" + toCharacter(value) + "'";
    }
}
