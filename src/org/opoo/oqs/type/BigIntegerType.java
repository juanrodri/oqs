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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * big_integer: A type that maps an SQL NUMERIC to a java.math.BigInteger
 *
 * @author Alex Lin
 * @version 1.0
 */
public class BigIntegerType extends ImmutableType implements LiteralType {
    public Object valueOf(String value) {
        return new BigInteger(value);
    }

    public Object get(ResultSet rs, String name) throws SQLException {
        //return rs.getBigDecimal(name).toBigIntegerExact(); this 1.5 only.
        BigDecimal bigDecimal = rs.getBigDecimal(name);
        return bigDecimal == null ? null :
                bigDecimal.setScale(0, BigDecimal.ROUND_UNNECESSARY).
                unscaledValue();
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        BigDecimal bigDecimal = rs.getBigDecimal(index);
        return bigDecimal == null ? null :
                bigDecimal.setScale(0, BigDecimal.ROUND_UNNECESSARY).
                unscaledValue();
    }

    public Class getReturnedClass() {
        return BigInteger.class;
    }

    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        st.setBigDecimal(index, new BigDecimal((BigInteger) value));
    }

    public int sqlType() {
        return Types.NUMERIC;
    }

    public String toString(Object value) {
        return value.toString();
    }

    public String toSQLString(Object value) throws Exception {
        return value.toString();
    }
}
