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

import java.sql.Connection;

import javax.sql.DataSource;

import org.opoo.oqs.CannotCreateQueryException;
import org.opoo.oqs.PreparedStatementBatcher;
import org.opoo.oqs.Query;
import org.opoo.oqs.StatementBatcher;
import org.opoo.oqs.core.AbstractQueryFactory;
import org.opoo.oqs.jdbc.ConnectionManager;
import org.opoo.oqs.jdbc.DataAccessException;
import org.opoo.oqs.spring.transaction.SpringJdbcTransactionFactory;
import org.opoo.oqs.transaction.Transaction;
import org.opoo.oqs.transaction.TransactionException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;


/**
 * 实现接口<tt>QueryFactory</tt>,JDBC基本操作使用Spring JdbcTemplate。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @see org.springframework.jdbc.core.JdbcTemplate
 * @since OQS1.0
 */
public class SpringQueryFactory extends AbstractQueryFactory implements
        InitializingBean {
    private JdbcTemplate jdbcTemplate;

    public SpringQueryFactory() {
    }

    public SpringQueryFactory(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public void setDataSource(DataSource ds) {
        super.setDataSource(ds);
        jdbcTemplate = createJdbcTemplate(ds);
    }

    protected JdbcTemplate createJdbcTemplate(DataSource dataSource) {
	    return new JdbcTemplate(dataSource);
    }

    public final void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }




    /**
     * 创建<tt>Query</tt>实例.
     *
     * @param queryString String
     * @return Query
     * @throws CannotCreateQueryException
     * @see SpringQueryImpl
     */
    public Query createQuery(String queryString) throws
            CannotCreateQueryException {
        return new SpringQuery(this, jdbcTemplate, queryString);
    }


    public StatementBatcher createBatcher() {
        return new SpringBatcher(this, jdbcTemplate);
    }

    public PreparedStatementBatcher createBatcher(String sql) {
        return new SpringBatcher(this, jdbcTemplate, sql);
    }


    public Transaction beginTransaction() throws TransactionException {
        return new SpringJdbcTransactionFactory().beginTransaction(
                getDataSource());
    }

    protected ConnectionManager createConnectionManager(final DataSource ds) {
        return new ConnectionManager() {
            public Connection getConnection() throws DataAccessException {
                return DataSourceUtils.getConnection(ds);
            }
            public void releaseConnection(Connection con) {
                DataSourceUtils.releaseConnection(con, ds);
            }
        };
    }



    /**
     * 支持Spring JDBC的ConnectionManager.
     *
     * @author Alex Lin(alex@opoo.org)
     * @version 1.0

       private static class SpringJdbcConnectionManager
        extends JdbcAccessor implements ConnectionManager
       {

      public SpringJdbcConnectionManager()
      {
      }

      public SpringJdbcConnectionManager(DataSource dataSource)
      {
        super(dataSource);
      }

      public Connection getConnection() throws DataAccessException
      {
        return DataSourceUtils.getConnection(getDataSource());
      }

      public void releaseConnection(Connection con)
      {
        DataSourceUtils.releaseConnection(con, getDataSource());
      }
       }*/
}
