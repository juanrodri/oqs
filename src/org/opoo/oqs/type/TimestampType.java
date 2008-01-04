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
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * <tt>timestamp</tt>: A type that maps an SQL TIMESTAMP to a Java
 * java.util.Date or java.sql.Timestamp.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class TimestampType extends MutableType {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";


    public Object valueOf(String value) {
        try {
            return new Timestamp(new SimpleDateFormat(TIMESTAMP_FORMAT).parse(
                    value).
                                 getTime());
        } catch (ParseException pe) {
            throw new RuntimeException("could not parse string", pe);
        }
    }


    public Object get(ResultSet rs, String name) throws SQLException {
        Timestamp ts = rs.getTimestamp(name);
        return ts; //new java.util.Date(ts.getTime());
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        Timestamp ts = rs.getTimestamp(index);
        return ts; //new java.util.Date(ts.getTime());
    }

    public Class getReturnedClass() {
        return java.util.Date.class;
    }


    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        Timestamp ts;
        if (value instanceof Timestamp) {
            ts = (Timestamp) value;
        } else {
            ts = new Timestamp(((java.util.Date) value).getTime());
        }
        st.setTimestamp(index, ts);
    }

    public int sqlType() {
        return Types.TIMESTAMP;
    }

    public String toString(Object value) {
        return new SimpleDateFormat(TIMESTAMP_FORMAT).format((java.util.Date)
                value);
    }
}
