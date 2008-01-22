package org.opoo.oqs.core;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.opoo.oqs.CannotCreateQueryException;
import org.opoo.oqs.PreparedStatementBatcher;
import org.opoo.oqs.Query;
import org.opoo.oqs.StatementBatcher;
import org.opoo.oqs.jdbc.ConnectionManager;
import org.opoo.oqs.jdbc.DataAccessException;
import org.opoo.oqs.jdbc.JdbcUtils;
import org.opoo.oqs.transaction.Transaction;
import org.opoo.oqs.transaction.TransactionException;

public class QueryFactoryImpl extends AbstractQueryFactory {
    public QueryFactoryImpl() {
    }

    /**
     * 取得当前数据源的Transaction并开始事务处理。
     *
     * @return Transaction
     * @throws TransactionException
     */
    public Transaction beginTransaction() throws TransactionException {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param sql String
     * @return PreparedStatementBatcher
     */
    public PreparedStatementBatcher createBatcher(String sql) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return StatementBatcher
     */
    public StatementBatcher createBatcher() {
        throw new UnsupportedOperationException();
    }

    /**
     * 创建一个Query实例。
     *
     * @param queryString String
     * @return Query
     * @throws CannotCreateQueryException
     */
    public Query createQuery(String queryString) throws
            CannotCreateQueryException {
        throw new UnsupportedOperationException();
    }

    protected ConnectionManager createConnectionManager(final DataSource ds) {
        return new ConnectionManager() {
            public Connection getConnection() throws DataAccessException {
                try {
                    return ds.getConnection();
                } catch (SQLException ex) {
                    throw new DataAccessException(ex);
                }
            }
            public void releaseConnection(Connection con) {
                JdbcUtils.close(con);
            }
        };
    }
}
