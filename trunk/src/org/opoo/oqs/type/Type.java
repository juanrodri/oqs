/*
 * $Id$
 *
 * Copyright 2006-2008 Alex Lin(alex@opoo.org). All rights reserved.
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Defines a mapping from a Java type to an JDBC datatype. This interface is intended to
 * be implemented by applications that need custom types.<br>
 * <br>
 * Implementors should usually be immutable and <b>must</b> certainly be threadsafe.
 *
 * @author Alex Lin
 * @version 1.0
 */
public interface Type extends java.io.Serializable {
    /**
     * The class returned by <tt>safeGet()</tt> methods. This is used to
     * establish the class of an array of this type.
     *
     * @return Class
     */
    Class getReturnedClass();

    /**
     *
     * @param value String
     * @return Object
     */
    Object valueOf(String value);

    /**
     * Retrieve an instance of the mapped class from a JDBC resultset. Implementations
     * should handle possibility of null values. This method might be called if the
     * type is known to be a single-column type.
     *
     * @param rs ResultSet
     * @param name String
     * @return Object
     * @throws SQLException
     */
    Object safeGet(ResultSet rs, String name) throws SQLException;

    /**
     *
     * @param rs ResultSet
     * @param index int
     * @return Object
     * @throws SQLException
     */
    Object safeGet(ResultSet rs, int index) throws SQLException;

    /**
     * Write an instance of the mapped class to a prepared statement, ignoring some columns.
     * Implementors should handle possibility of null values. A multi-column type should be
     * written to parameters starting from <tt>index</tt>.
     *
     * @param st PreparedStatement
     * @param v Object
     * @param index int
     * @throws SQLException
     */
    void safeSet(PreparedStatement st, Object v, int index) throws SQLException;

    /**
     *
     * @return int
     */
    int sqlType();


    ////////////////////////////////////////////////////////////
    /// All types
    ///////////////////////////////////////////////////////////
    /**
     * Data <tt>long</tt> type.
     */
    NullableType LONG = new LongType();
    /**
     * Data <tt>short</tt> type.
     */
    NullableType SHORT = new ShortType();
    /**
     * Data <tt>integer</tt> type.
     */
    NullableType INTEGER = new IntegerType();
    /**
     * Data <tt>byte</tt> type.
     */
    NullableType BYTE = new ByteType();
    /**
     * Data <tt>float</tt> type.
     */
    NullableType FLOAT = new FloatType();
    /**
     * Data <tt>double</tt> type.
     */
    NullableType DOUBLE = new DoubleType();
    /**
     * Data <tt>character</tt> type.
     */
    NullableType CHARACTER = new CharacterType();
    /**
     * Data <tt>string</tt> type.
     */
    NullableType STRING = new StringType();
    /**
     * Data <tt>time</tt> type.
     */
    NullableType TIME = new TimeType();
    /**
     * Data <tt>date</tt> type.
     */
    NullableType DATE = new DateType();
    /**
     * Data <tt>timestamp</tt> type.
     */
    NullableType TIMESTAMP = new TimestampType();

    /**
     * Data <tt>boolean</tt> type.
     */
    NullableType BOOLEAN = new BooleanType();
    /**
     * Data <tt>true_false</tt> type.
     */
    NullableType TRUE_FALSE = new TrueFalseType();
    /**
     * Data <tt>yes_no</tt> type.
     */
    NullableType YES_NO = new YesNoType();
    /**
     * Data <tt>big_decimal</tt> type.
     */
    NullableType BIG_DECIMAL = new BigDecimalType();
    /**
     * Data <tt>big_integer</tt> type.
     */
    NullableType BIG_INTEGER = new BigIntegerType();
    /**
     * Data <tt>binary</tt> type.
     */
    NullableType BINARY = new BinaryType();
    /**
     * Data <tt>text</tt> type.
     */
    NullableType TEXT = new TextType();
    /**
     * Data <tt>blob</tt> type.
     */
    Type BLOB = new BlobType();
    /**
     * Data <tt>clob</tt> type.
     */
    Type CLOB = new ClobType();
    /**
     * Data <tt>calendar</tt> type.
     */
    NullableType CALENDAR = new CalendarType();
    /**
     * Data <tt>calendar_date</tt> type.
     */
    NullableType CALENDAR_DATE = new CalendarDateType();
    /**
     * Data <tt>locale</tt> type.
     */
    NullableType LOCALE = new LocaleType();
    /**
     * Data <tt>currency</tt> type.
     */
    NullableType CURRENCY = new CurrencyType();
    /**
     * Data <tt>timezone</tt> type.
     */
    NullableType TIMEZONE = new TimeZoneType();
    /**
     * Data <tt>class</tt> type.
     */
    NullableType CLASS = new ClassType();
    /**
     * Data <tt>serializable</tt> type.
     */
    NullableType SERIALIZABLE = new SerializableType(
            Serializable.class);
    /**
     * Data <tt>object</tt> type.
     */
    Type OBJECT = new AnyType();
}
