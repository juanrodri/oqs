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

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <tt>currency</tt>: A type that maps an SQL VARCHAR to a
 * <tt>java.util.Currency</tt>
 * @see java.util.Currency
 *
 * @author Alex Lin
 * @version 1.0
 */
public class CurrencyType extends ImmutableType implements LiteralType {
    public static final Class CURRENCY_CLASS;
    private static final Method CURRENCY_GET_INSTANCE;
    private static final Method CURRENCY_GET_CODE;

    static {
        Class clazz;
        try {
            clazz = Class.forName("java.util.Currency");
        } catch (ClassNotFoundException cnfe) {
            clazz = null;
        }
        if (clazz == null) {
            CURRENCY_CLASS = null;
            CURRENCY_GET_INSTANCE = null;
            CURRENCY_GET_CODE = null;
        } else {
            CURRENCY_CLASS = clazz;
            try {
                CURRENCY_GET_INSTANCE = clazz.getMethod("getInstance",
                        new Class[] {String.class});
                CURRENCY_GET_CODE = clazz.getMethod("getCurrencyCode",
                        new Class[0]);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Exception in static initializer of CurrencyType", e);
            }
        }
    }


    public Object valueOf(String value) {
        try {
            return CURRENCY_GET_INSTANCE.invoke(null, new Object[] {value});
        } catch (Exception e) {
            throw new RuntimeException("Could not resolve currency code: " +
                                       value);
        }
    }


    public Object get(ResultSet rs, String name) throws SQLException {
        String code = (String) Type.STRING.safeGet(rs, name);
        return result(rs, code);
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        String code = (String) Type.STRING.safeGet(rs, index);
        return result(rs, code);
    }

    private Object result(ResultSet rs, String code) {
        try {
            return (code == null) ? null :
                    CURRENCY_GET_INSTANCE.invoke(null, new Object[] {code});
        } catch (Exception e) {
            throw new RuntimeException("Could not resolve currency code: " +
                                       code);
        }
    }


    public Class getReturnedClass() {
        return CURRENCY_CLASS;
    }


    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        Object code;
        try {
            code = CURRENCY_GET_CODE.invoke(value, null);
        } catch (Exception e) {
            throw new RuntimeException("Could not get Currency code", e);
        }
        Type.STRING.set(st, code, index);
    }


    public int sqlType() {
        return Type.STRING.sqlType();
    }


    public String toString(Object value) {
        try {
            return (String) CURRENCY_GET_CODE.invoke(value, null);
        } catch (Exception e) {
            throw new RuntimeException("Could not get Currency code", e);
        }
    }

    public String toSQLString(Object value) throws Exception {
        String code;
        try {
            code = (String) CURRENCY_GET_CODE.invoke(value, null);
        } catch (Exception e) {
            throw new RuntimeException("Could not get Currency code", e);
        }
        return ((LiteralType) Type.STRING).toSQLString(code);
    }
}
