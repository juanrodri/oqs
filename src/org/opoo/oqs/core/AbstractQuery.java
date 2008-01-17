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
package org.opoo.oqs.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.Mapper;
import org.opoo.oqs.Query;
import org.opoo.oqs.QueryException;
import org.opoo.oqs.QueryFactory;
import org.opoo.oqs.QueryIterator;
import org.opoo.oqs.TypedValue;
import org.opoo.oqs.dialect.Dialect;
import org.opoo.oqs.jdbc.ConnectionManager;
import org.opoo.oqs.jdbc.ResultSetHandler;
import org.opoo.oqs.type.Type;
import org.opoo.oqs.type.TypeFactory;
import org.opoo.util.Assert;
import org.opoo.util.ClassUtils;
import org.opoo.util.CollectionUtils;
import org.opoo.util.StringUtils;


/**
 * 抽象类，实现了Query接口的大部分基本操作。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since OQS1.0
 */
public abstract class AbstractQuery implements Query {
    private static final Log log = LogFactory.getLog(Query.class);

    private static final Object UNSET_PARAMETER = "<unset parameter>";
    private static final Object UNSET_TYPE = "<unset type>";

    //原始信息
    private final int debugLevel;
    private final AbstractQueryFactory queryFactory;
    private final Dialect dialect;
    private final String queryString;


    //处理后信息
    private List values = new ArrayList(4);
    private List types = new ArrayList(4);
    private int positionalParameterCount = 0;
    private Set actualNamedParameters = new HashSet(4);
    private Map namedParameters = new HashMap(4);
    private Map namedParameterLists = new HashMap(4);
    private boolean readOnly;
    private String comment;

    //表示未初始化
    private int maxResults = -1, firstResult = 0;

    private String operation = null;
    private boolean isSelect = false;
    private boolean isCall = false;
    private boolean isUpdate = false;

    private boolean isComplexMapper = false;
    private boolean isTranslated = false;
    private Mapper mapper = null;
    private String sql = null;


    //临时的变量
    String msql;
    int maxRows = 0, fetchSize = 0, queryTimeout = 0;


    public AbstractQuery(String queryString, AbstractQueryFactory queryFactory) {
        this.dialect = queryFactory.dialect;
        this.debugLevel = queryFactory.debugLevel;
        this.queryFactory = queryFactory;
        this.queryString = queryString;
        this.msql = queryString;

        if (debugLevel > 1) {
            //System.out.println("[RET]: " + resultType);
            System.out.println("[SQS]: " + queryString);
        }
        initialize();
    }

    private void initialize() {
        QueryStringHelper.verifyQueryString(msql);
        initParameterBookKeeping();
    }

    protected void translateMsqlIfNecessary() {
        if (isSelect && mapper == null && !isTranslated) {
            QueryTranslator translator = new DefaultQueryTranslator(msql,
                    queryFactory.beanClassLoader);
            msql = translator.getSQLString();
            mapper = translator.getMapper();
            isTranslated = true;
        }
    }

    /**
     * 在执行查询前要做的处理。
     */
    protected void before() {
        translateMsqlIfNecessary();
        //verifyParameters();
        //System.out.println(values);
        verifyPositionalParameters(false);
        verifyNamedParameters();
        processSQL(false);
        processLimitSQL(); //分页之类的
        verifyPositionalParametersAfterProcessSQL();
    }

    /**
     * 在执行查询之后要做的处理。
     */
    protected void after() {
        setMaxRows(0);
        setQueryTimeout(0);
        setFetchSize(0);
    }


    protected abstract List doList() throws QueryException;

    //protected abstract Iterator doIterate() throws QueryException;

    protected abstract int doUpdate() throws QueryException;

    protected abstract Object doCall() throws QueryException;


    protected void check(boolean b) {
        if (b) {
            throw new QueryException("The called method cannot handle '" +
                                     operation + "'", msql);
        }
    }

    public List list() throws QueryException {
        check(!isSelect);
        before();
        try {
            return doList();
        } finally {
            after();
        }
    }

    public QueryIterator iterate() throws QueryException {
        check(!isSelect);
        before();
        try {
            //return doIterate();
            return iterate(getSql(), valueArray(), typeArray());
        } finally {
            after();
        }
    }

    public int executeUpdate() throws QueryException {
        check(!isUpdate);
        before();
        return doUpdate();
    }

    public Serializable insert() throws QueryException {
        checkDialect();
        check(!isUpdate);
        if (!queryString.trim().toUpperCase().startsWith("INSERT")) {
            throw new QueryException(
                    "Insert() use for insert new row and get generated id only.",
                    sql);
        }

        before();
        //return doInsert();

        String qs = getSql();
        //System.out.println(qs);
        if (dialect.supportsInsertSelectIdentity()) {
            qs = dialect.appendIdentitySelectToInsert(qs);
            if (debugLevel > 1) {
                System.out.println("[IQS]: " + qs);
            }
        }

        //if(true) return null;
        return getInsertSelectIdentity(qs);
    }


    public Object call() throws QueryException {
        check(!isCall);
        before();
        try {
            return doCall();
        } finally {
            after();
        }
    }

    public int[] batch() {
        throw new UnsupportedOperationException();
    }

    private QueryIterator iterate(String sql, Object[] params, Type[] types) throws
            QueryException {
        try {
            final ConnectionManager manager = getQueryFactory().
                                              getConnectionManager();
            final Connection conn = manager.getConnection();
            final PreparedStatement ps = conn.prepareStatement(sql);

            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    types[i].safeSet(ps, params[i], i + 1);
                }
            }
            applyStatementSettings(ps);

            final ResultSet rs = ps.executeQuery();

            return createIterator(rs, ps, conn, manager);
        } catch (Exception ex) {
            throw new QueryException("iterate error:" + sql, ex);
        }
    }

    private QueryIterator createIterator(ResultSet rs,
                                         PreparedStatement ps,
                                         Connection conn,
                                         ConnectionManager cm) throws
            SQLException {
        mapper.initialize(rs.getMetaData());
        advance(rs);
        return new QueryIterator(rs, ps, conn, cm, mapper);
    }


    protected QueryFactory getQueryFactory() {
        return queryFactory;
    }


/////////////
    protected void checkDialect() {
        Assert.notNull(dialect, "被调用方法要求必须设置Dialect。");
        //if(this.dialect == null)
        //{
        //throw new QueryException("被调用方法要求dialect不能为null。");
        //}
    }

    public final String getQueryString() {
        return queryString;
    }

    public final String getSql() {
        if (debugLevel > 0) {
            System.out.println("[SQL]: " + sql);
        }
        return sql;
    }


    /**
     * 返回所有命名参数和值。
     *
     * @return Map
     */
    protected Map getNamedParams() {
        return new HashMap(namedParameters);
    }


    /**
     * 处理查询SQL。
     * 将Query String处理成SQL，主要操作是替换其中的Named parameters。
     * <p>:namedparam ==> ?
     *
     * @param reserveFirstParameter boolean
     * @throws QueryException
     */
    private void processSQL(boolean reserveFirstParameter) throws
            QueryException {
        if (actualNamedParameters.size() == 0) {
            sql = msql;
            return;
        }

        Map namedParams = getNamedParams();
        String qs = bindParameterLists(namedParams);

        if (debugLevel > 1) {
            System.out.println("[TQS]: " + qs);
        }
        //System.out.println(namedParams);

        List theValues = new ArrayList();
        List theTypes = new ArrayList();
        int startIndex = reserveFirstParameter ? 1 : 0;
        int valuesIndex = startIndex;
        int typesIndex = startIndex;

        StringTokenizer st = new StringTokenizer(qs,
                                                 QueryStringHelper.
                                                 QS_SEPARATORS);
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            //System.out.println(str);
            if (str.startsWith("?")) {
                theValues.add(values.get(valuesIndex++));
                theTypes.add(types.get(typesIndex++));
            } else if (str.startsWith(QueryStringHelper.QS_VARIABLE_PREFIX)) {
                qs = qs.replaceAll(str, "?");
                TypedValue vt = (TypedValue) namedParams.get(str.substring(1));
                if (vt == null) {
                    throw new QueryException(
                            "Process named parameter exception.",
                            str.substring(1));
                }
                theValues.add(vt.getValue());
                theTypes.add(vt.getType());
            }
        }

        values = theValues;
        types = theTypes;
        sql = qs;

        //System.out.println("[SQL] " + sql);
        //System.out.println("[values] " + theValues);
        //System.out.println("[types] " + theTypes);
    }

    /**
     * 在处理过SQL后检查POSIONAL PARAMETERS的完整性与正确性。
     * 参数个数是否等于参数值的个数？
     */
    private void verifyPositionalParametersAfterProcessSQL() {
        if (sql == null) {
            throw new QueryException("Build sql exception, query string", msql);
        }
        int positionCount = StringUtils.countUnquoted(sql, '?');

        if (positionCount == 0) {
            return;
        }
        int i = types.size();
        i = values.size();

        if (positionCount != types.size()
            || positionCount != values.size()
            || types.size() != values.size()) {
            throw new QueryException(
                    "Expected positional parameter count: " +
                    (positionCount) +
                    ", actual parameters: " +
                    values,
                    sql
                    );
        }
    }

    /**
     * 检查参数值的正确性。检查是否有positional parameter没有被赋值。
     *
     * @param reserveFirstParameter boolean 参数的index是从1开始还是从0开始?
     * @throws QueryException
     */
    private void verifyPositionalParameters(boolean reserveFirstParameter) throws
            QueryException {
        if (positionalParameterCount == 0) {
            return;
        }

        int positionalValueCount = 0;
        for (int i = 0; i < values.size(); i++) {
            Object object = types.get(i);
            if (values.get(i) == UNSET_PARAMETER || object == UNSET_TYPE) {
                if (reserveFirstParameter && i == 0) {
                    continue;
                } else {
                    throw new QueryException(
                            "Unset positional parameter at position: " +
                            i, getQueryString());
                }
            }
            positionalValueCount++;
        }

        if (positionalParameterCount != positionalValueCount) {
            if (reserveFirstParameter &&
                positionalParameterCount - 1 != positionalValueCount) {
                throw new QueryException(
                        "Expected positional parameter count: " +
                        (positionalParameterCount - 1) +
                        ", actual parameters: " +
                        values,
                        getQueryString()
                        );
            } else if (!reserveFirstParameter) {
                throw new QueryException(
                        "Expected positional parameter count: " +
                        positionalParameterCount +
                        ", actual parameters: " +
                        values,
                        getQueryString()
                        );
            }
        }
    }

    /**
     * 检查命名参数是否完整。
     * @throws QueryException
     */
    private void verifyNamedParameters() throws QueryException {
        if (actualNamedParameters.size() == 0) {
            return;
        }

        if (actualNamedParameters.size() !=
            namedParameters.size() + namedParameterLists.size()) {
            Set missingParams = new HashSet(actualNamedParameters);
            missingParams.removeAll(namedParameterLists.keySet());
            missingParams.removeAll(namedParameters.keySet());
            throw new QueryException("Not all named parameters have been set: " +
                                     missingParams, getQueryString());
        }
    }


    /////////////////////////////////////////
    //Page Limit, timeout, fetchsize eg.
    /**
     * 如果有分页查询，根据页面信息及数据库种类的不同生成相应的页面查询SQL。
     */
    private void processLimitSQL() {
        //Dialect dialect = getDialect();
        //暂时不管，不处理分页的部分
        if (dialect == null || !isSelect) { //非查询
            log.debug("do not process limit sql:dialect=" + dialect +
                      ", isSelect=" + isSelect);
            return;
        }

        boolean useLimit = dialect.supportsLimit() && maxResults > 0;
        boolean hasFirstRow = firstResult > 0;
        boolean useOffset = hasFirstRow && useLimit &&
                            dialect.supportsLimitOffset();

        if (useLimit) {
            //process SQL
            sql = dialect.getLimitString(sql.trim(), //use of trim() here is ugly?
                                         useOffset ? firstResult : 0,
                                         dialect.useMaxForLimit() ?
                                         maxResults + firstResult : maxResults
                  );
            //System.out.println(sql);
            //process Parameters
            appendLimitParameters();
            //appendLimitParameters(dialect);
        } else {
            if (maxResults > 0) {
                setMaxRows(maxResults + firstResult);
            }
        }
        //st.setQueryTimeout( getTimeout());
        //st.setFetchSize( getFetchSize());
    }


    private void appendLimitParameters() {
        if (!dialect.supportsVariableLimit()) {
            return;
        }
        if (maxResults == -1) {
            throw new QueryException("no max results set");
        }
        int firstRow = firstResult;
        int lastRow = dialect.useMaxForLimit() ? maxResults + firstResult :
                      maxResults;
        boolean hasFirstRow = firstRow > 0 && dialect.supportsLimitOffset();
        boolean reverse = dialect.bindLimitParametersInReverseOrder();
        /**
         * boolean hasFirstRow = firstRow > 0 && dialect.supportsLimitOffset();
         boolean reverse = dialect.bindLimitParametersInReverseOrder();
         if ( hasFirstRow ) st.setInt( index + ( reverse ? 1 : 0 ), firstRow );
         st.setInt( index + ( reverse || !hasFirstRow ? 0 : 1 ), lastRow );
                    return hasFirstRow ? 2 : 1;

         */
        /*
             List limitValues = new ArrayList(1);
             List limitTypes = new ArrayList(1);
             if (hasFirstRow && !reverse)
             {
          limitValues.add(new Integer(firstRow));
          limitTypes.add(Type.INTEGER);
             }
             limitValues.add(new Integer(lastRow));
             limitTypes.add(Type.INTEGER);
             if (hasFirstRow && reverse)
             {
          limitValues.add(new Integer(firstRow));
          limitTypes.add(Type.INTEGER);
             }*/
        /*
             Integer[] rows = new Integer[2];
             if ( hasFirstRow ) rows[reverse ? 1 : 0]= new Integer(firstRow);
             rows[(reverse || !hasFirstRow) ? 0 : 1] = new Integer(lastRow);
             //int size = hasFirstRow ? 2 : 1;
             List limitValues = new ArrayList(1);
             List limitTypes = new ArrayList(1);
             if(hasFirstRow)
             {
          limitValues.addAll(Arrays.asList(rows));
          CollectionUtils.fill(limitTypes, Type.INTEGER, 2);
             }
             else
             {
          limitValues.add(rows[0]);
          limitTypes.add(Type.INTEGER);
             }
         */

        List limitValues = new ArrayList(1);
        List limitTypes = new ArrayList(1);
        Integer first = new Integer(firstRow);
        Integer last = new Integer(lastRow);
        if (hasFirstRow && !reverse) {
            limitValues.add(first);
        }
        limitValues.add(last);
        if (hasFirstRow && reverse) {
            limitValues.add(first);
        }
        CollectionUtils.fill(limitTypes, Type.INTEGER, hasFirstRow ? 2 : 1);

        //插在开头还是结尾
        if (dialect.bindLimitParametersFirst()) {
            limitValues.addAll(values);
            limitTypes.addAll(types);
            values = limitValues;
            types = limitTypes;
        } else {
            values.addAll(limitValues);
            types.addAll(limitTypes);
        }
    }

    /**
     * 返回所有命名参数及值。
     * @return Map
     */
    protected Map getNamedParameterLists() {
        return namedParameterLists;
    }

    /**
     * 返回参数参数值的List。
     * @return List
     */
    protected List getValues() {
        return values;
    }

    /**
     * 返回查询参数类型的List。
     * @return List
     */
    protected List getTypes() {
        return types;
    }


    public Query setMaxResults(int maxResults) {
        checkDialect();
        this.maxResults = maxResults;
        return this;
    }

    public Query setFirstResult(int firstResult) {
        checkDialect();
        this.firstResult = firstResult;
        return this;
    }

    /**
     * 设置最大行数。
     * @param maxRows int
     */
    protected void setMaxRows(int maxRows) {
        // preparedStatment.setMaxRows(10);
        //return this;
        this.maxRows = maxRows;
    }

    public Query setQueryTimeout(int timeout) {
        // preparedStatment.setQueryTimeout(10);
        this.queryTimeout = timeout;
        return this;
    }

    public Query setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        //preparedStatment.setFetchSize(10);
        return this;
    }

    protected int getFetchSize() {
        return fetchSize;
    }

    protected int getMaxRows() {
        return maxRows;
    }

    protected int getQueryTimeout() {
        return queryTimeout;
    }


    protected void applyStatementSettings(Statement stmt) throws SQLException {
        int fetchSize = getFetchSize();
        if (fetchSize > 0) {
            stmt.setFetchSize(fetchSize);
        }
        int maxRows = getMaxRows();
        if (maxRows > 0) {
            stmt.setMaxRows(maxRows);
        }
        int timeout = getQueryTimeout();
        if (timeout > 0) {
            stmt.setQueryTimeout(timeout);
        }
    }


    public Query setParameter(int position, Object val, Type type) {
        if (positionalParameterCount == 0) {
            throw new IllegalArgumentException(
                    "No positional parameters in query: "
                    + getQueryString());
        }
        if (position < 0 || position > positionalParameterCount - 1) {
            throw new IllegalArgumentException(
                    "Positional parameter does not exist: " + position +
                    " in query: " +
                    getQueryString());
        }
        int size = values.size();
        if (position < size) {
            values.set(position, val);
            types.set(position, type);
        } else {
            // prepend value and type list with null for any positions before the wanted position.
            for (int i = 0; i < position - size; i++) {
                values.add(UNSET_PARAMETER);
                types.add(UNSET_TYPE);
            }
            values.add(val);
            types.add(type);
        }
        return this;
    }

    public Query setString(int position, String val) {
        setParameter(position, val, Type.STRING);
        return this;
    }

    public Query setCharacter(int position, char val) {
        setParameter(position, new Character(val), Type.CHARACTER);
        return this;
    }

    public Query setBoolean(int position, boolean val) {
        setParameter(position, val ? Boolean.TRUE : Boolean.FALSE,
                     Type.BOOLEAN);
        return this;
    }

    public Query setByte(int position, byte val) {
        setParameter(position, new Byte(val), Type.BYTE);
        return this;
    }

    public Query setShort(int position, short val) {
        setParameter(position, new Short(val), Type.SHORT);
        return this;
    }

    public Query setInteger(int position, int val) {
        setParameter(position, new Integer(val), Type.INTEGER);
        return this;
    }

    public Query setLong(int position, long val) {
        setParameter(position, new Long(val), Type.LONG);
        return this;
    }

    public Query setFloat(int position, float val) {
        setParameter(position, new Float(val), Type.FLOAT);
        return this;
    }

    public Query setDouble(int position, double val) {
        setParameter(position, new Double(val), Type.DOUBLE);
        return this;
    }

    public Query setBinary(int position, byte[] val) {
        setParameter(position, val, Type.BINARY);
        return this;
    }

    public Query setText(int position, String val) {
        setParameter(position, val, Type.TEXT);
        return this;
    }

    public Query setSerializable(int position, Serializable val) {
        setParameter(position, val, Type.SERIALIZABLE);
        return this;
    }

    public Query setDate(int position, Date date) {
        setParameter(position, date, Type.DATE);
        return this;
    }

    public Query setTime(int position, Date date) {
        setParameter(position, date, Type.TIME);
        return this;
    }

    public Query setTimestamp(int position, Date date) {
        setParameter(position, date, Type.TIMESTAMP);
        return this;
    }

    public Query setLocale(int position, Locale locale) {
        setParameter(position, locale, Type.LOCALE);
        return this;
    }

    public Query setCalendar(int position, Calendar calendar) {
        setParameter(position, calendar, Type.CALENDAR);
        return this;
    }

    public Query setCalendarDate(int position, Calendar calendar) {
        setParameter(position, calendar, Type.CALENDAR_DATE);
        return this;
    }

    public Query setBinary(String name, byte[] val) {
        setParameter(name, val, Type.BINARY);
        return this;
    }

    public Query setText(String name, String val) {
        setParameter(name, val, Type.TEXT);
        return this;
    }

    public Query setBoolean(String name, boolean val) {
        setParameter(name, val ? Boolean.TRUE : Boolean.FALSE, Type.BOOLEAN);
        return this;
    }

    public Query setByte(String name, byte val) {
        setParameter(name, new Byte(val), Type.BYTE);
        return this;
    }

    public Query setCharacter(String name, char val) {
        setParameter(name, new Character(val), Type.CHARACTER);
        return this;
    }

    public Query setDate(String name, Date date) {
        setParameter(name, date, Type.DATE);
        return this;
    }

    public Query setDouble(String name, double val) {
        setParameter(name, new Double(val), Type.DOUBLE);
        return this;
    }

    public Query setFloat(String name, float val) {
        setParameter(name, new Float(val), Type.FLOAT);
        return this;
    }

    public Query setInteger(String name, int val) {
        setParameter(name, new Integer(val), Type.INTEGER);
        return this;
    }

    public Query setLocale(String name, Locale locale) {
        setParameter(name, locale, Type.LOCALE);
        return this;
    }

    public Query setCalendar(String name, Calendar calendar) {
        setParameter(name, calendar, Type.CALENDAR);
        return this;
    }

    public Query setCalendarDate(String name, Calendar calendar) {
        setParameter(name, calendar, Type.CALENDAR_DATE);
        return this;
    }

    public Query setLong(String name, long val) {
        setParameter(name, new Long(val), Type.LONG);
        return this;
    }

    public Query setParameter(String name, Object val, Type type) {
        //System.out.println(type.getClass());
        if (!actualNamedParameters.contains(name)) {
            throw new IllegalArgumentException("Parameter " + name +
                                               " does not exist as a named parameter in [" +
                                               getQueryString() + "]");
        } else {
            namedParameters.put(name, new TypedValue(val, type));
            return this;
        }
    }

    public Query setSerializable(String name, Serializable val) {
        setParameter(name, val, Type.SERIALIZABLE);
        return this;
    }

    public Query setShort(String name, short val) {
        setParameter(name, new Short(val), Type.SHORT);
        return this;
    }

    public Query setString(String name, String val) {
        setParameter(name, val, Type.STRING);
        return this;
    }

    public Query setTime(String name, Date date) {
        setParameter(name, date, Type.TIME);
        return this;
    }

    public Query setTimestamp(String name, Date date) {
        setParameter(name, date, Type.TIMESTAMP);
        return this;
    }

    public Query setBigDecimal(int position, BigDecimal number) {
        setParameter(position, number, Type.BIG_DECIMAL);
        return this;
    }

    public Query setBigDecimal(String name, BigDecimal number) {
        setParameter(name, number, Type.BIG_DECIMAL);
        return this;
    }

    public Query setBigInteger(int position, BigInteger number) {
        setParameter(position, number, Type.BIG_DECIMAL);
        return this;
    }

    public Query setBigInteger(String name, BigInteger number) {
        setParameter(name, number, Type.BIG_DECIMAL);
        return this;
    }

    public Query setParameter(int position, Object val) throws QueryException {
        if (val == null) {
            setParameter(position, val, Type.SERIALIZABLE);
        } else {
            setParameter(position, val, guessType(val));
        }
        return this;
    }

    public Query setParameter(String name, Object val) throws QueryException {
        if (val == null) {
            setParameter(name, val, Type.SERIALIZABLE);
        } else {
            setParameter(name, val, guessType(val));
        }
        return this;
    }

    private static Type guessType(Object param) throws QueryException {
        //Class clazz = param.getClass();
        //DataTypeProxyHelper.getClassWithoutInitializingProxy(param);
        return TypeFactory.guessType(param);
    }

    public Type[] getReturnTypes() throws QueryException {
        return null;
    }

    public String[] getReturnAliases() throws QueryException {
        return null;
    }

    public Query setParameterList(String name, Collection vals, Type type) throws
            QueryException {
        if (vals.size() == 0) {
            throw new QueryException("Collection must not be empty!",
                                     "Parameter named " + name);
        }
        if (!actualNamedParameters.contains(name)) {
            throw new IllegalArgumentException("Parameter " + name +
                                               " does not exist as a named parameter in [" +
                                               getQueryString() + "]");
        }
        namedParameterLists.put(name, new TypedValue(vals, type));
        return this;
    }

    /**
     * 绑定所有参数值是Collection类型的命名参数。
     * <p>例如:
     * <pre>
     * qs: select id from table1 where name in (:names)
     * query.setParameterList("names", new String[]{"d", "g", "h"});
     * 经过这步骤处理后：
     * qs: select id from table1 where name in (:names0_, :names1_, :names2_);
     * </pre>
     *
     * @param namedParamsCopy Map
     * @return String
     */
    protected String bindParameterLists(Map namedParamsCopy) {
        String query = this.msql;
        Iterator iter = namedParameterLists.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry me = (Map.Entry) iter.next();
            query = bindParameterList(query, (String) me.getKey(),
                                      (TypedValue) me.getValue(),
                                      namedParamsCopy);
        }
        return query;
    }


    /**
     * 处理每一个绑定参数值是Collection类型的命名参数。
     * @param query String
     * @param name String
     * @param typedList TypedValue
     * @param namedParamsCopy Map
     * @return String
     */
    private static String bindParameterList(String query, String name,
                                            TypedValue typedList,
                                            Map namedParamsCopy) {
        Collection vals = (Collection) typedList.getValue();
        Type type = typedList.getType();
        StringBuffer list = new StringBuffer(16);
        Iterator iter = vals.iterator();
        int i = 0;
        while (iter.hasNext()) {
            String alias = name + i++ +'_';
            namedParamsCopy.put(alias, new TypedValue(iter.next(), type));
            list.append(QueryStringHelper.QS_VARIABLE_PREFIX + alias);
            if (iter.hasNext()) {
                list.append(", ");
            }
        }
        //return StringHelper.replace(query, ParserHelper.HQL_VARIABLE_PREFIX + name,
        //                            list.toString(), true);
        return query.replaceAll(QueryStringHelper.QS_VARIABLE_PREFIX + name,
                                list.toString());
    }

    public Query setParameterList(String name, Collection vals) throws
            QueryException {
        if (vals == null) {
            throw new QueryException("Collection must be not null!",
                                     "parameter named " + name);
        }

        if (vals.size() == 0) {
            setParameterList(name, vals, null);
        } else {
            setParameterList(name, vals, guessType(vals.iterator().next()));
        }

        return this;
    }

    public String[] getNamedParameters() throws QueryException {
        return (String[]) actualNamedParameters.toArray(new String[
                actualNamedParameters.size()]);
    }

    public Query setProperties(Object bean) throws QueryException {
        Class clazz = bean.getClass();
        String[] params = getNamedParameters();
        for (int i = 0; i < params.length; i++) {
            String namedParam = params[i];
            final Object object = ClassUtils.getProperty(bean, namedParam);
            if (object == null) {
//        throw new QueryException(
//            "Set bean's properties for named parameters error."
//            + "can not find property:" + namedParam);
                setParameter(namedParam, null, Type.SERIALIZABLE);
            } else if (object instanceof Collection) {
                setParameterList(namedParam, (Collection) object);
            } else if (object.getClass().isArray()) {
                setParameterList(namedParam, (Object[]) object);
            } else {
                setParameter(namedParam, object, guessType(object));
            }
        }
        return this;
    }

    public Query setParameterList(String name, Object[] vals, Type type) throws
            QueryException {
        return setParameterList(name, Arrays.asList(vals), type);
    }

    public Query setParameterList(String name, Object[] vals) throws
            QueryException {
        return setParameterList(name, Arrays.asList(vals));
    }

    public Object uniqueResult() throws QueryException {
        return uniqueElement(list());
    }

    static Object uniqueElement(List list) throws QueryException {
        int size = list.size();
        if (size == 0) {
            return null;
        }
        Object first = list.get(0);
        for (int i = 1; i < size; i++) {
            if (list.get(i) != first) {
                throw new QueryException("Result not unique. size is " +
                                         list.size());
            }
        }
        return first;
    }

    /**
     * 返回所有参数值类型的数组。
     * @return Type[]
     */
    public Type[] typeArray() {
        return (Type[]) getTypes().toArray(new Type[getTypes().size()]);
    }

    /**
     * 返回所有参数值的SQL类型的数组。
     * @return int[]
     * @see java.sql.Types
     */
    public int[] sqlType() {
        Type[] types = typeArray();
        int[] a = new int[types.length];
        for (int i = 0; i < types.length; i++) {
            a[i] = types[i].sqlType();
        }
        return a;
    }

    /**
     * 返回所有参数的值。
     * @return Object[]
     */
    public Object[] valueArray() {
        return getValues().toArray();
    }

    public Query setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public String toString() {
        return StringUtils.unqualify(getClass().getName()) + '(' + msql +
                ')';
    }

    public Query setParameters(Object[] values, Type[] types) {
        /**
         * Arrays.asList产生的list不能执行addAll
         */
        this.values = new ArrayList(Arrays.asList(values));
        this.types = new ArrayList(Arrays.asList(types));
        return this;
    }

    public Query setParameters(Object[] values) {
        this.values = new ArrayList(Arrays.asList(values));
        this.types.clear();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                this.types.add(Type.SERIALIZABLE);
            } else {
                this.types.add(guessType(values[i]));
            }
        }
        return this;
    }


    public Query setParameters(String[] names, Object[] values) {
        if (names.length != values.length) {
            throw new IllegalArgumentException(
                    "named parameters count must match values count.");
        }
        for (int i = 0; i < names.length; i++) {
            if (values[i] != null) {
                if (values[i] instanceof Collection) {
                    setParameterList(names[i], (Collection) values[i]);
                    continue;
                } else if (values[i].getClass().isArray()) {
                    setParameterList(names[i], (Object[]) values[i]);
                    continue;
                }
            }
            setParameter(names[i], values[i]);
        }
        return this;
    }

    /**
     * 查询结果是否Read only?
     * @return boolean
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    public Query setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }


    /**
     * 初始化query，获得queryString中named parameter的name数组、positional parameter
     * 的个数、是否是4中基本的数据操作、初步判断queryString是否使用了Querist查询？
     */
    private void initParameterBookKeeping() {
        StringTokenizer st = new StringTokenizer(msql,
                                                 QueryStringHelper.
                                                 QS_SEPARATORS);
        Set result = new HashSet();

        //add start
        operation = st.nextToken();
        if ("from".equalsIgnoreCase(operation)) {
            msql = "select * " + msql;
            operation = "select";
            isSelect = true;
        } else if ("select".equalsIgnoreCase(operation)) {
            isSelect = true;
        } else if ("insert".equalsIgnoreCase(operation)
                   || "update".equalsIgnoreCase(operation)
                   || "delete".equalsIgnoreCase(operation)) {
            isUpdate = true;
        } else if (operation.startsWith("{")) {
            isCall = true;
        } else {
            throw new QueryException(
                    "Syntax error, unsupport manipulate symbol '"
                    + operation + "'", msql);
        }
        //add end

        while (st.hasMoreTokens()) {
            String string = st.nextToken();
            //System.out.println(string);
            if (string.startsWith(QueryStringHelper.QS_VARIABLE_PREFIX)) {
                result.add(string.substring(1));
            }

            ///add
            if (!isComplexMapper && "new".equalsIgnoreCase(string)) {
                isComplexMapper = true;
            }
        }

        //
        if (isComplexMapper && !isSelect) {
            throw new QueryException("ComplexMapper query for select only,"
                                     + " can not use for execute update.",
                                     msql);
        }

        //
        actualNamedParameters = result;
        positionalParameterCount = StringUtils.countUnquoted(msql, '?');
    }


    protected ResultSetHandler createListResultSetHandler() {
        ResultSetHandler lrsh = new ListResultSetHandler(getMapper());
        return createResultSetHandler(lrsh);
    }

    protected ResultSetHandler createIteratorResultSetHandler() {
        ResultSetHandler lrsh = new ListResultSetHandler(getMapper());
        return createResultSetHandler(lrsh);
    }

    protected ResultSetHandler createResultSetHandler(final ResultSetHandler
            rsh) {
        return new ResultSetHandler() {
            public Object handle(ResultSet rs) throws SQLException {
                mapper.initialize(rs.getMetaData());
                advance(rs);
                return rsh.handle(rs);
            }
        };
    }


    /**
     * 处理与分页有关的数据
     *
     * @param rs ResultSet
     * @throws SQLException
     */
    private void advance(ResultSet rs) throws SQLException {
        if (dialect == null) {
            return;
        }

        boolean useLimit = dialect.supportsLimit() && maxResults > 0;
        if (!dialect.supportsLimitOffset() || !useLimit) {
            int firstRow = firstResult;
            if (firstRow > 0) {
                try {
                    // we can go straight to the first required row
                    rs.absolute(firstRow);
                    log.debug("using absolute");
                } catch (Exception e) {
                    // we need to step through the rows one row at a time (slow)
                    for (int m = 0; m < firstRow; m++) {
                        rs.next();
                    }
                }
            }
        }
    }


    protected Mapper getMapper() {
        Assert.notNull(mapper, "mapper is required");
        return mapper;
    }

    public Query setMapper(Mapper mapper) throws QueryException {
        this.mapper = mapper;
        return this;
    }


    //////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

    /**
     * 从插入成功后返回的ResultSet中抽取数据库自动生成的ID。
     *
     * @param sql String
     * @return Serializable
     */
    private Serializable getInsertSelectIdentity(String sql) {
        try {
            ConnectionManager manager = queryFactory.getConnectionManager();
            Connection conn = queryFactory.getConnectionManager().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            try {
                Object values[] = valueArray();
                for (int i = 0; i < values.length; i++) {
                    Object value = values[i];
                    if (value == null) {
                        Type.SERIALIZABLE.safeSet(ps, value, i + 1);
                    } else {
                        TypeFactory.guessType(value).safeSet(ps, value, i + 1);
                    }
                }
                if (dialect.supportsInsertSelectIdentity()) {
                    if (!ps.execute()) {
                        while (!ps.getMoreResults() &&
                               ps.getUpdateCount() != -1) {
                            continue; // Do nothing (but stop checkstyle from complaining).
                        }
                    }
                    //note early exit!
                    ResultSet rs = ps.getResultSet();
                    try {
                        return getGeneratedIdentity(rs);
                    } finally {
                        //JdbcUtils.closeResultSet(rs);
                        close(rs, null);
                    }
                }
                //else if (Settings.isGetGeneratedKeysEnabled())
                //{
                //  ps.executeUpdate();
                //  //note early exit!
                //  return getGeneratedIdentity(ps.getGeneratedKeys());
                //}
                //else
                //{
                //  ps.executeUpdate();
                //need post insert then select identity
                //postInsert.append("NEED");
                //}

                //替代以上的else if 和else
                else {
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();

                    if (rs != null) {
                        //使用getGeneratedKeys()函数获取key
                        try {
                            log.debug("Using getGeneratedKeys() to get keys.");
                            return getGeneratedIdentity(rs);
                        } finally {
                            close(rs, null);
                        }
                    }
                    //else通过getPostInsertGeneratedIndentity来查询
                }
            } finally {
                //JdbcUtils.closeStatement(ps);
                close(null, ps);
                //JdbcUtils.closeConnection(conn);
                //如果不关闭，则不能查询PostInsertGeneratedIndentiry
                manager.releaseConnection(conn);
            }
        } catch (SQLException ex) {
            throw new QueryException("could not insert: ", ex);
        }
        return getPostInsertGeneratedIndentity();
    }

    private void close(ResultSet rs, Statement stmt) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                log.debug("Could not close JDBC ResultSet", ex);
            } catch (Throwable ex) {
                // We don't trust the JDBC driver: It might throw RuntimeException or Error.
                log.debug("Unexpected exception on closing JDBC ResultSet", ex);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception ex) {
                log.debug("Could not close JDBC Statement", ex);
            } catch (Throwable ex) {
                // We don't trust the JDBC driver: It might throw RuntimeException or Error.
                log.debug("Unexpected exception on closing JDBC Statement", ex);
            }
        }
    }

    /**
     * 从插入成功后返回的ResultSet中抽取数据库自动生成的ID。
     * @param rs ResultSet
     * @return Serializable
     * @throws QueryException
     * @throws SQLException
     */
    private Serializable getGeneratedIdentity(ResultSet rs) throws
            QueryException, SQLException {
        if (!rs.next()) {
            throw new QueryException(
                    "The database returned no natively generated identity value");
        }
        //final Serializable id = IdentifierGeneratorFactory.get( rs, type );
        Serializable id = (Serializable) rs.getObject(1);
        if (id != null) {
            if (id instanceof String) {
                return id.toString();
            }
            if (id instanceof Number) {
                return id;
            }
            //只支持字符串和Number别的出错。
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("Natively generated identity: " + id);
        }
        return id;
    }

    /**
     * getPostInsertGeneratedIndentiry
     *
     * @return Serializable
     */
    private Serializable getPostInsertGeneratedIndentity() {
        final String sql = dialect.getIdentitySelectString(null, null,
                Types.OTHER);
        return (Serializable) queryFactory.createQuery(sql).uniqueResult();
    }
}


/**
 *
 * <p>Title: Framework</p>
 *
 * <p>Description: orm</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
class ListResultSetHandler implements ResultSetHandler {
    private final Mapper mapper;
    private final int rowsExpected;
    public ListResultSetHandler(Mapper mapper) {
        this(mapper, 0);
    }

    public ListResultSetHandler(Mapper mapper, int rowsExpected) {
        Assert.notNull(mapper, "mapper is required");
        this.mapper = mapper;
        this.rowsExpected = rowsExpected;
    }

    public Object handle(ResultSet rs) throws SQLException {
        List results = (this.rowsExpected > 0 ? new ArrayList(this.rowsExpected) :
                        new ArrayList());
        int rowNum = 0;
        while (rs.next()) {
            results.add(mapper.map(rs, rowNum++));
        }
        return results;
    }
}


class IteratorResultSetHandler implements ResultSetHandler {
    private final Mapper mapper;
    public IteratorResultSetHandler(Mapper mapper) {
        Assert.notNull(mapper, "mapper is required");
        this.mapper = mapper;
    }

    public Object handle(ResultSet rs) throws SQLException {
        return null;
    }
}
