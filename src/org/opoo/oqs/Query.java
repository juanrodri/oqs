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
package org.opoo.oqs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.opoo.oqs.type.Type;


/**
 * An object-oriented representation of a OQS query. A <tt>Query</tt>
 * instance is obtained by calling <tt>QueryFactory.createQuery()</tt>.
 *
 * <ul>
 * <li>a particular page of the result set may be selected by calling <tt>
 * setMaxResults(), setFirstResult()</tt>
 * <li>named query parameters may be used
 * </ul>
 * <br>
 * Named query parameters are tokens of the form <tt>:name</tt> in the
 * query string. A value is bound to the <tt>integer</tt> parameter
 * <tt>:foo</tt> by calling<br>
 * <br>
 * <tt>setParameter("foo", foo, Type.INTEGER);</tt><br>
 * <br>
 * for example. A name may appear multiple times in the query string.<br>
 * <br>
 * JDBC-style <tt>?</tt> parameters are also supported. To bind a
 * value to a JDBC-style parameter use a set method that accepts an
 * <tt>int</tt> positional argument (numbered from zero, contrary
 * to JDBC).<br>
 * <br>
 * You may not mix and match JDBC-style parameters and named parameters
 * in the same query.<br>
 * <br>
 * Queries are executed by calling <tt>list()</tt> or
 * <tt>iterate()</tt>. A query may be re-executed by subsequent invocations.
 * Its lifespan is, however, bounded by the lifespan of the <tt>Session</tt>
 * that created it.<br>
 * <br>
 * Implementors are not intended to be threadsafe.
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since OQS1.0
 */
public interface Query {
    /**
     * Get the SQL string that actually execute by JDBC driver
     *
     * @return String
     */
    //String getSql();

    /**
     * Get the query string.
     *
     * @return the query string
     */
    String getQueryString();


    /**
     * Return the names of all named parameters of the query.
     * @return the parameter names, in no particular order
     * @throws QueryException
     */
    String[] getNamedParameters() throws QueryException;

    /**
     * Return the query results as an <tt>QueryIterator</tt>. If the query
     * contains multiple results pre row, the results are returned in
     * an instance of <tt>Object[]</tt>, <tt>List</tt>, <tt>Map</tt>
     * or other entity Objects.
     * <br>
     * <br>
     *
     * @return the result iterator
     * @throws QueryException
     */
    QueryIterator iterate() throws QueryException;

    /**
     * Return the query results as a <tt>List</tt>. If the query contains
     * multiple results pre row, the results are returned in an instance
     * of <tt>Object[]</tt>, <tt>List</tt>, <tt>Map</tt> or other entity Objects.
     *
     * @return the result list
     * @throws QueryException
     */
    List list() throws QueryException;


    /**
     * Convenience method to return a single instance that matches
     * the query, or null if the query returns no results.
     *
     * @return the single result or <tt>null</tt>
     * @throws QueryException if there is more than one matching result
     */
    Object uniqueResult() throws QueryException;

    /**
     * Execute the update or delete statement.
     * </p>
     * The semantics are compliant with the ejb3 Query.executeUpdate()
     * method.
     *
     * @return The number of entities updated or deleted.
     * @throws QueryException
     */
    int executeUpdate() throws QueryException;

    /**
     *
     * @return int[]
     * @throws QueryException
     */
    //int[] batch() throws QueryException;

    /**
     * Set the maximum number of rows to retrieve. If not set,
     * there is no limit to the number of rows retrieved.
     * @param maxResults the maximum number of rows
     * @return Query
     */
    Query setMaxResults(int maxResults);

    /**
     * Set the first row to retrieve. If not set, rows will be
     * retrieved beginnning from row <tt>0</tt>.
     * @param firstResult a row number, numbered from <tt>0</tt>
     * @return Query
     */
    Query setFirstResult(int firstResult);

    /**
     * Entities retrieved by this query will be loaded in
     * a read-only mode where Querist will never dirty-check
     * them or make changes persistent.
     * @param readOnly boolean
     * @return Query
     */
    Query setReadOnly(boolean readOnly);

    /**
     * Set a timeout for the underlying JDBC query.
     * @param timeout int the timeout in seconds
     * @return Query
     */
    Query setQueryTimeout(int timeout);

    /**
     * Set a fetch size for the underlying JDBC query.
     * @param fetchSize the fetch size
     * @return Query
     */
    Query setFetchSize(int fetchSize);

    /**
     * Add a comment to the generated SQL.
     * @param comment a human-readable string
     * @return Query
     */
    Query setComment(String comment);

    /**
     * Bind a value to a JDBC-style query parameter.
     * @param position the position of the parameter in the query
     * string, numbered from <tt>0</tt>.
     * @param val the possibly-null parameter value
     * @param type the Querist type
     * @return Query
     */
    Query setParameter(int position, Object val, Type type);

    /**
     * Bind a value to a named query parameter.
     * @param name the name of the parameter
     * @param val the possibly-null parameter value
     * @param type the Querist type
     * @return Query
     */
    Query setParameter(String name, Object val, Type type);

    /**
     * Bind a value to a JDBC-style query parameter, guessing the
     * Querist type from the class of the given object.
     * @param position the position of the parameter in the query
     * string, numbered from <tt>0</tt>.
     * @param val the non-null parameter value
     * @return Query
     * @throws QueryException if no type could be determined
     */
    Query setParameter(int position, Object val) throws QueryException;

    /**
     * Bind a value to a named query parameter, guessing the Querist
     * type from the class of the given object.
     * @param name the name of the parameter
     * @param val the non-null parameter value
     * @return Query
     * @throws QueryException if no type could be determined
     */
    Query setParameter(String name, Object val) throws QueryException;


    /**
     * Bind values and types to positional parameters.
     * @param values Object[]
     * @param types Type[]
     * @return Query
     * @throws QueryException
     */
    Query setParameters(Object[] values, Type[] types) throws QueryException;

    /**
     * Bind values to positional parameters.
     * @param values Object[]
     * @return Query
     * @throws QueryException
     */
    Query setParameters(Object[] values) throws QueryException;

    /**
     * Bind values to named parameters.
     *
     * @param names String[]
     * @param values Object[]
     * @return Query
     * @throws QueryException
     */
    Query setParameters(String[] names, Object[] values) throws QueryException;

    /**
     * Bind multiple values to a named query parameter. This is useful for binding
     * a list of values to an expression such as <tt>foo.bar in (:value_list)</tt>.
     * @param name the name of the parameter
     * @param vals a collection of values to list
     * @param type the Querist type of the values
     * @return Query
     * @throws QueryException
     */
    Query setParameterList(String name, Collection vals, Type type) throws
            QueryException;

    /**
     * Bind multiple values to a named query parameter, guessing the Querist type from the
     * class of the first object in the collection. This is useful for binding a list of values
     * to an expression such as <tt>foo.bar in (:value_list)</tt>.
     * @param name the name of the parameter
     * @param vals a collection of values to list
     * @return Query
     * @throws QueryException
     */
    Query setParameterList(String name, Collection vals) throws
            QueryException;

    /**
     * Bind multiple values to a named query parameter. This is useful for binding
     * a list of values to an expression such as <tt>foo.bar in (:value_list)</tt>.
     * @param name the name of the parameter
     * @param vals a collection of values to list
     * @param type the Querist type of the values
     * @return Query
     * @throws QueryException
     */
    Query setParameterList(String name, Object[] vals, Type type) throws
            QueryException;

    /**
     * Bind multiple values to a named query parameter, guessing the Querist type from the
     * class of the first object in the array. This is useful for binding a list of values
     * to an expression such as <tt>foo.bar in (:value_list)</tt>.
     * @param name the name of the parameter
     * @param vals a collection of values to list
     * @return Query
     * @throws QueryException
     */
    Query setParameterList(String name, Object[] vals) throws
            QueryException;

    /**
     * Bind the property values of the given bean to named parameters of the query,
     * matching property names with parameter names and mapping property types to
     * Querist types using hueristics.
     * @param bean any JavaBean or POJO
     * @return Query
     * @throws QueryException
     */
    Query setProperties(Object bean) throws QueryException;

    Query setString(int position, String val);

    Query setCharacter(int position, char val);

    Query setBoolean(int position, boolean val);

    Query setByte(int position, byte val);

    Query setShort(int position, short val);

    Query setInteger(int position, int val);

    Query setLong(int position, long val);

    Query setFloat(int position, float val);

    Query setDouble(int position, double val);

    Query setBinary(int position, byte[] val);

    Query setText(int position, String val);

    Query setSerializable(int position, Serializable val);

    Query setLocale(int position, Locale locale);

    Query setBigDecimal(int position, BigDecimal number);

    Query setBigInteger(int position, BigInteger number);

    Query setDate(int position, Date date);

    Query setTime(int position, Date date);

    Query setTimestamp(int position, Date date);

    Query setCalendar(int position, Calendar calendar);

    Query setCalendarDate(int position, Calendar calendar);

    Query setString(String name, String val);

    Query setCharacter(String name, char val);

    Query setBoolean(String name, boolean val);

    Query setByte(String name, byte val);

    Query setShort(String name, short val);

    Query setInteger(String name, int val);

    Query setLong(String name, long val);

    Query setFloat(String name, float val);

    Query setDouble(String name, double val);

    Query setBinary(String name, byte[] val);

    Query setText(String name, String val);

    Query setSerializable(String name, Serializable val);

    Query setLocale(String name, Locale locale);

    Query setBigDecimal(String name, BigDecimal number);

    Query setBigInteger(String name, BigInteger number);

    Query setDate(String name, Date date);

    Query setTime(String name, Date date);

    Query setTimestamp(String name, Date date);

    Query setCalendar(String name, Calendar calendar);

    Query setCalendarDate(String name, Calendar calendar);

    /**
     *
     * @param mapper Mapper
     * @return Query
     * @throws QueryException
     */
    Query setMapper(Mapper mapper) throws QueryException;

    /**
     * 插入一条由数据库生成ID的数据，并返回ID
     * @return Serializable
     * @throws QueryException
     */
    Serializable insert() throws QueryException;

    /**
     * 调用存储过程。
     *
     * @return Object
     * @throws QueryException
     */
    Object call() throws QueryException;
}
