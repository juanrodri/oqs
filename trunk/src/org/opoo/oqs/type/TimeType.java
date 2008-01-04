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
import java.sql.Time;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * <tt>time</tt>: A type that maps an SQL TIME to a Java
 * java.util.Date or java.sql.Time.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class TimeType extends MutableType {


    public Object valueOf(String value) {
        try {
            return new SimpleDateFormat().parse(value);
        } catch (ParseException pe) {
            throw new RuntimeException("could not parse string", pe);
        }
    }


    public Object get(ResultSet rs, String name) throws SQLException {
        return rs.getTime(name);
    }


    public Object get(ResultSet rs, int index) throws SQLException {
        return rs.getTime(index);
    }


    public Class getReturnedClass() {
        return java.util.Date.class;
    }


    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        Time time;
        if (value instanceof Time) {
            time = (Time) value;
        } else {
            time = new Time(((java.util.Date) value).getTime());
        }
        st.setTime(index, time);
    }


    public int sqlType() {
        return Types.TIME;
    }

    public String toString(Object value) {
        return new SimpleDateFormat("HH:mm:ss").format((java.util.Date) value);
    }
}
