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

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * <tt>blob</tt>: A type that maps an SQL BLOB to a java.sql.Blob.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class BlobType implements Type {

    public Object valueOf(String value) {
        throw new UnsupportedOperationException("todo");
    }

    public Class getReturnedClass() {
        return Blob.class;
    }


    public boolean isMutable() {
        return false;
    }

    public Object safeGet(ResultSet rs, String name) throws SQLException {
        Blob value = rs.getBlob(name);
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
        Blob value = rs.getBlob(index);
        return value;
    }

    public void safeSet(PreparedStatement st, Object value, int index) throws
            SQLException {
        if (value == null) {
            st.setNull(index, Types.BLOB);
        } else {
            st.setBlob(index, (Blob) value);
        }
    }

    public int sqlType() {
        return Types.BLOB;
    }
}
