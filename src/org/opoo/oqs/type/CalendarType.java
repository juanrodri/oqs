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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <tt>calendar</tt>: A type mapping for a <tt>Calendar</tt> object that
 * represents a datetime.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class CalendarType extends MutableType {

    public Object valueOf(String value) {
        Calendar result = new GregorianCalendar();
        result.setTime(((Date) Type.TIMESTAMP.valueOf(value)));
        return result;
    }

    public Object get(ResultSet rs, String name) throws SQLException {
        Timestamp ts = rs.getTimestamp(name);
        if (ts != null) {
            Calendar cal = new GregorianCalendar();

            //if (Environment.jvmHasTimestampBug())
            //{
            //  cal.setTime(new Date(ts.getTime() + ts.getNanos() / 1000000));
            //}
            //else
            //{
            cal.setTime(ts);
            //}
            return cal;
        } else {
            return null;
        }
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        Timestamp ts = rs.getTimestamp(index);
        if (ts != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(ts);
            return cal;
        } else {
            return null;
        }
    }

    public Class getReturnedClass() {
        return Calendar.class;
    }

    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        final Calendar cal = (Calendar) value;
        //st.setTimestamp( index,  new Timestamp( cal.getTimeInMillis() ), cal ); //JDK 1.5 only
        st.setTimestamp(index, new Timestamp(cal.getTime().getTime()), cal);
    }

    public int sqlType() {
        return Types.TIMESTAMP;
    }

    public String toString(Object value) {
        return Type.TIMESTAMP.toString(((Calendar) value).getTime());
    }
}
