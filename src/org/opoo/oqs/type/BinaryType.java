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
 * binary: A type that maps an SQL VARBINARY to a Java byte[].
 *
 * @author Alex Lin
 * @version 1.0
 */
public class BinaryType extends MutableType {
    public Object valueOf(String value) {
        if (value == null) {
            return null;
        }
        if (value.length() % 2 != 0) {
            throw new IllegalArgumentException(
                    "The string is not a valid xml representation of a binary content.");
        }
        byte[] bytes = new byte[value.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            String hexStr = value.substring(i * 2, (i + 1) * 2);
            bytes[i] = (byte) (Integer.parseInt(hexStr, 16) + Byte.MIN_VALUE);
        }
        return bytes;
    }

    public Object get(ResultSet rs, String name) throws SQLException {
        return rs.getBytes(name);
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        return rs.getBytes(index);
    }

    public Class getReturnedClass() {
        return byte[].class;
    }

    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        st.setBytes(index, (byte[]) value);
    }

    public int sqlType() {
        return Types.VARBINARY;
    }


    public String toString(Object value) {
        byte[] bytes = (byte[]) value;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hexStr = Integer.toHexString(bytes[i] - Byte.MIN_VALUE);
            if (hexStr.length() == 1) {
                buf.append('0');
            }
            buf.append(hexStr);
        }
        return buf.toString();
    }
}
