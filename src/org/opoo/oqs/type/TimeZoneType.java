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
import java.util.TimeZone;

/**
 * <tt>timezone</tt>: A type that maps an SQL VARCHAR to a
 * <tt>java.util.TimeZone</tt>
 * @see java.util.TimeZone
 *
 * @author Alex Lin
 * @version 1.0
 */
public class TimeZoneType extends ImmutableType implements LiteralType {


    public Object valueOf(String value) {
        return TimeZone.getTimeZone(value);
    }


    public Object get(ResultSet rs, String name) throws SQLException {
        String id = (String) Type.STRING.safeGet(rs, name);
        return (id == null) ? null : TimeZone.getTimeZone(id);
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        String id = (String) Type.STRING.safeGet(rs, index);
        return (id == null) ? null : TimeZone.getTimeZone(id);
    }

    public Class getReturnedClass() {
        return TimeZone.class;
    }


    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        Type.STRING.set(st, ((TimeZone) value).getID(), index);
    }


    public int sqlType() {
        return Type.STRING.sqlType();
    }


    public String toString(Object value) {
        return ((TimeZone) value).getID();
    }

    public String toSQLString(Object value) throws Exception {
        return ((LiteralType) Type.STRING).toSQLString(
                ((TimeZone) value).getID()
                );
    }
}
