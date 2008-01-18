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
package org.opoo.oqs.spring;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.Query;
import org.opoo.oqs.QueryException;
import org.opoo.oqs.core.AbstractQuery;
import org.opoo.oqs.core.AbstractQueryFactory;
import org.opoo.oqs.jdbc.ResultSetHandler;
import org.opoo.oqs.spring.jdbc.ArgTypePreparedStatementSetter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;


/**
 * 使用Spring <tt>JdbcTemplate</tt>基本JDBC操作实现了<tt>Query</tt>接口。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @see org.springframework.jdbc.core.JdbcTemplate
 * @since OQS1.0
 */
public class SpringQueryImpl extends AbstractQuery {
    private JdbcTemplate jdbcTemplate;
    private static final Log log = LogFactory.getLog(SpringQueryImpl.class);

    public SpringQueryImpl(String queryString,
                           AbstractQueryFactory queryFactory) {
        super(queryString, queryFactory);
    }

    public SpringQueryImpl(String queryString,
                           AbstractQueryFactory queryFactory,
                           JdbcTemplate jdbcTemplate) {
        super(queryString, queryFactory);
        setJdbcTemplate(jdbcTemplate);
    }

    protected ResultSetExtractor createResultSetExtractor(final
            ResultSetHandler rsh) {
        return new ResultSetExtractor() {
            public Object extractData(ResultSet resultSet) throws SQLException,
                    DataAccessException {
                return rsh.handle(resultSet);
            }
        };
    }

    /**
     * doExecuteUpdate
     *
     * @return int
     * @throws QueryException
     * @see AbstractQuery#executeUpdate
     */
    protected int doUpdate() throws QueryException {
        return jdbcTemplate.update(getSql(), valueArray(), sqlType());
        //return 0;
    }

    /**
     * doIterate
     *
     * @return Iterator
     * @throws QueryException
     * @see AbstractQuery#iterate
     */
    /*
     protected Iterator doIterate() throws QueryException
       {
      return doList().iterator();
       }*/

    /**
     * doList
     *
     * @return List
     * @throws QueryException
     * @see AbstractQuery#list
     */
    protected List doList() throws QueryException {
        //return (List) jdbcTemplate.query(getSql(), valueArray(), new ListResultSetExtractor(this));
        final PreparedStatementSetter pss = new ArgTypePreparedStatementSetter(
                valueArray(), typeArray());
        //return (List) jdbcTemplate.query(getSql(), valueArray(), createResultSetExtractor(createListResultSetHandler()));
        return (List) jdbcTemplate.query(getSql(), pss,
                                         createResultSetExtractor(
                                                 createListResultSetHandler()));
    }

    protected Object doCall() throws QueryException {
        final PreparedStatementSetter pss = new ArgTypePreparedStatementSetter(
                valueArray(), typeArray());
        final ResultSetExtractor rse = createResultSetExtractor(
                createListResultSetHandler());
        return jdbcTemplate.execute(getSql(), new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement
                                                callableStatement) throws
                    SQLException, DataAccessException {

                if (getQueryTimeout() > 0) {
                    callableStatement.setQueryTimeout(getQueryTimeout());
                }

                pss.setValues(callableStatement);

                boolean retVal = callableStatement.execute();
                int updateCount = callableStatement.getUpdateCount();
                if (log.isDebugEnabled()) {
                    log.debug("CallableStatement.execute() returned '" + retVal +
                              "'");
                    log.debug("CallableStatement.getUpdateCount() returned " +
                              updateCount);
                }

                ResultSet rs = callableStatement.getResultSet();
                try {
                    if (rs != null && rse != null) {
                        return rse.extractData(rs);
                    }
                } finally {
                    JdbcUtils.closeResultSet(rs);
                }

                if (updateCount > 0) {
                    return new Integer(updateCount);
                }
                return null;
            }
        });
    }


    /**
     *
     * @param jdbcTemplate JdbcTemplate
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Sets results max rows
     * @param i int
     * @see JdbcTemplate#setMaxRows
     */
    protected void setMaxRows(int i) {
        super.setMaxRows(i);
        jdbcTemplate.setMaxRows(i);
    }

    /**
     * Set JDBC connection query fetch seize
     * @param fetchSize int
     * @return Query
     * @see JdbcTemplate#setFetchSize
     */
    public Query setFetchSize(int fetchSize) {
        jdbcTemplate.setFetchSize(fetchSize);
        return this;
    }

    /**
     *
     * @return Query
     */
    public Query setQueryTimeout(int timeout) {
        super.setQueryTimeout(timeout);
        jdbcTemplate.setQueryTimeout(timeout);
        return this;
    }
}
