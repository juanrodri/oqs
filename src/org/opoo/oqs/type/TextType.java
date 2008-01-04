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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * <tt>text</tt>: A type that maps an SQL CLOB to a Java String.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class TextType extends ImmutableType {


    public Object valueOf(String value) {
        return value;
    }

    public Object result(ResultSet rs, Reader charReader) throws SQLException {
        // Retrieve the value of the designated column in the current row of this
        // ResultSet object as a java.io.Reader object
        //Reader charReader = rs.getCharacterStream(name);

        // if the corresponding SQL value is NULL, the reader we got is NULL as well
        if (charReader == null) {
            return null;
        }

        // Fetch Reader content up to the end - and put characters in a StringBuffer
        StringBuffer sb = new StringBuffer();
        try {
            char[] buffer = new char[2048];
            while (true) {
                int amountRead = charReader.read(buffer, 0, buffer.length);
                if (amountRead == -1) {
                    break;
                }
                sb.append(buffer, 0, amountRead);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("IOException occurred reading text", ioe);
        } finally {
            try {
                charReader.close();
            } catch (IOException e) {
                throw new RuntimeException(
                        "IOException occurred closing stream", e);
            }
        }

        // Return StringBuffer content as a large String
        return sb.toString();
    }

    public Object get(ResultSet rs, String name) throws SQLException {
        Reader charReader = rs.getCharacterStream(name);
        return result(rs, charReader);
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        Reader charReader = rs.getCharacterStream(index);
        return result(rs, charReader);
    }


    public Class getReturnedClass() {
        return String.class;
    }


    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        String str = (String) value;
        st.setCharacterStream(index, new StringReader(str), str.length());
    }


    public int sqlType() {
        return Types.CLOB; //or Types.LONGVARCHAR?
    }

    public String toString(Object value) {
        return (String) value;
    }
}
