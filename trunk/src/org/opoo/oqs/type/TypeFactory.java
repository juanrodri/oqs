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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Used internally to obtain instances of <tt>Type</tt>. Applications should
 *  use static methods.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class TypeFactory {

    private static final Map BASIC_TYPES;

    static {
        Map basics = new HashMap();
        basics.put(boolean.class.getName(), Type.BOOLEAN);
        basics.put(long.class.getName(), Type.LONG);
        basics.put(short.class.getName(), Type.SHORT);
        basics.put(int.class.getName(), Type.INTEGER);
        basics.put(byte.class.getName(), Type.BYTE);
        basics.put(float.class.getName(), Type.FLOAT);
        basics.put(double.class.getName(), Type.DOUBLE);
        basics.put(char.class.getName(), Type.CHARACTER);
        basics.put(Boolean.class.getName(), Type.BOOLEAN);
        basics.put(Long.class.getName(), Type.LONG);
        basics.put(Short.class.getName(), Type.SHORT);
        basics.put(Integer.class.getName(), Type.INTEGER);
        basics.put(Byte.class.getName(), Type.BYTE);
        basics.put(Float.class.getName(), Type.FLOAT);
        basics.put(Double.class.getName(), Type.DOUBLE);
        basics.put(Character.class.getName(), Type.CHARACTER);
        basics.put(String.class.getName(), Type.STRING);
        basics.put(java.util.Date.class.getName(), Type.TIMESTAMP);
        basics.put(Time.class.getName(), Type.TIME);
        basics.put(Timestamp.class.getName(), Type.TIMESTAMP);
        basics.put(java.sql.Date.class.getName(), Type.DATE);
        basics.put(BigDecimal.class.getName(), Type.BIG_DECIMAL);
        basics.put(BigInteger.class.getName(), Type.BIG_INTEGER);
        basics.put(Locale.class.getName(), Type.LOCALE);
        basics.put(Calendar.class.getName(), Type.CALENDAR);
        basics.put(GregorianCalendar.class.getName(), Type.CALENDAR);
        if (CurrencyType.CURRENCY_CLASS != null) {
            basics.put(CurrencyType.CURRENCY_CLASS.getName(),
                       Type.CURRENCY);
        }
        basics.put(TimeZone.class.getName(), Type.TIMEZONE);
        basics.put(Object.class.getName(), Type.OBJECT);
        basics.put(Class.class.getName(), Type.CLASS);
        basics.put(byte[].class.getName(), Type.BINARY);
        basics.put("byte[]", Type.BINARY);
        basics.put(Blob.class.getName(), Type.BLOB);
        basics.put(Clob.class.getName(), Type.CLOB);
        basics.put(Serializable.class.getName(), Type.SERIALIZABLE);
        BASIC_TYPES = Collections.unmodifiableMap(basics);
    }


    /**
     * Given the name of a Querist basic type, return an instance of
     * <tt>Type</tt>.
     * @param name String
     * @return Type
     */
    public static Type basic(String name) {
        return (Type) BASIC_TYPES.get(name);
    }

    /**
     * 根据对象返回对象的类型。
     * @param object Object
     * @return Type
     */
    public static Type guessType(Object object) {
        Class clazz = object.getClass();
        return guessType(clazz);
    }

    public static Type guessType(Class clazz) {
        String classname = clazz.getName();
        return guessType(classname);
    }

    /**
     * 根据对象的类名称返回类型。
     * @param classname String
     * @return Type
     */
    public static Type guessType(String classname) {
        //System.out.println("Guess type: " + classname);
        return (Type) BASIC_TYPES.get(classname);
    }

    public static Type safeGuess(Class clazz) {
        Type type = guessType(clazz);
        if (type == null) {
            return Type.OBJECT;
        }
        return type;
    }

    /**
     * An <tt>any</tt> type.
     *
     * @param metaType       a type mapping <tt>java.lang.Class</tt> to a single column
     * @param identifierType the entity identifier type
     * @return the Type
     */
    public static Type any(Type metaType, Type identifierType) {
        return new AnyType(metaType, identifierType);
    }
}
