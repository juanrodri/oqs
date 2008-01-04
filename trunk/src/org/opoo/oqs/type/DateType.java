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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * <tt>date</tt>: A type that maps an SQL DATE to a Java Date.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class DateType extends MutableType {

    public Object valueOf(String value) {
        try {
            return new SimpleDateFormat().parse(value);
        } catch (ParseException pe) {
            throw new RuntimeException("could not parse String", pe);
        }
    }

    public Object get(ResultSet rs, String name) throws SQLException {
        return rs.getDate(name);
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        return rs.getDate(index);
    }

    public Class getReturnedClass() {
        return java.util.Date.class;
    }

    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        Date sqlDate;
        if (value instanceof Date) {
            sqlDate = (Date) value;
        } else {
            sqlDate = new Date(((java.util.Date) value).getTime());
        }
        st.setDate(index, sqlDate);
    }


    public int sqlType() {
        return Types.DATE;
    }


    public String toString(Object value) {
        return new SimpleDateFormat("yyyy-MM-dd").format((java.util.Date) value);
    }
}
