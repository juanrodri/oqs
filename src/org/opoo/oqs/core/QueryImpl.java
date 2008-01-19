package org.opoo.oqs.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.QueryException;
import org.opoo.oqs.jdbc.ConnectionManager;
import org.opoo.oqs.jdbc.JdbcUtils;
import org.opoo.oqs.jdbc.ResultSetHandler;
import org.opoo.oqs.type.Type;
import org.opoo.oqs.type.TypeFactory;

public class QueryImpl extends AbstractQuery {

    private static final Log log = LogFactory.getLog(QueryImpl.class);

    private final ConnectionManager cm;
    QueryImpl(String queryString, AbstractQueryFactory queryFactory) {
        super(queryString, queryFactory);
        cm = queryFactory.getConnectionManager();
    }

    /**
     * doCall
     *
     * @return Object
     * @throws QueryException
     */
    protected Object doCall() throws QueryException {
        return (List) execute(new CallableStatementCallback() {
            public Object doInStatement(CallableStatement cs) throws
                    SQLException {
                if (getQueryTimeout() > 0) {
                    cs.setQueryTimeout(getQueryTimeout());
                }
                boolean retVal = cs.execute();
                int updateCount = cs.getUpdateCount();
                if (log.isDebugEnabled()) {
                    log.debug("CallableStatement.execute() returned '" + retVal +
                              "'");
                    log.debug("CallableStatement.getUpdateCount() returned " +
                              updateCount);
                }
                ResultSet rs = cs.getResultSet();
                ResultSetHandler rsh = createListResultSetHandler();
                try {
                    if (rs != null && rsh != null) {
                        return rsh.handle(rs);
                    }
                } finally {
                    JdbcUtils.close(rs);
                }

                if (updateCount > 0) {
                    return new Integer(updateCount);
                }
                return null;
            }
        });
    }

    /**
     * doList
     *
     * @return List
     * @throws QueryException
     */
    protected List doList() throws QueryException {
        return (List) execute(new PreparedStatementCallback() {
            public Object doInStatement(PreparedStatement st) throws
                    SQLException {
                ResultSet rs = st.executeQuery();
                try {
                    return createListResultSetHandler().handle(rs);
                } finally {
                    JdbcUtils.close(st);
                }
            }
        });
    }

    /**
     * doUpdate
     *
     * @return int
     * @throws QueryException
     */
    protected int doUpdate() throws QueryException {
        return ((Integer) execute(new PreparedStatementCallback() {
            public Object doInStatement(PreparedStatement st) throws
                    SQLException {
                return new Integer(st.executeUpdate());
            }
        })).intValue();
    }

    void setValues(PreparedStatement pt) throws SQLException {
        final Object[] values = valueArray();
        final Type[] types = typeArray();
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                types[i].safeSet(pt, values[i], i + 1);
            }
        }
    }

    Object execute(CallableStatementCallback csc) {
        Connection conn = cm.getConnection();
        CallableStatement cs = null;
        try {
            cs = conn.prepareCall(getSql());
            setValues(cs);
            return csc.doInStatement(cs);
        } catch (SQLException ex) {
            throw new QueryException(ex);
        } finally {
            JdbcUtils.close(cs);
            cm.releaseConnection(conn);
        }
    }

    Object execute(PreparedStatementCallback psc) {
        Connection conn = cm.getConnection();
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(getSql());
            setValues(st);
            return psc.doInStatement(st);
        } catch (SQLException ex) {
            throw new QueryException(ex);
        } finally {
            JdbcUtils.close(st);
            cm.releaseConnection(conn);
        }
    }

    protected Object queryForObject(String sql, Class clazz) {
        Connection conn = cm.getConnection();
        PreparedStatement st = null;
	ResultSet rs = null;
        try {
            st = conn.prepareStatement(sql);
	    rs = st.executeQuery();
	    if(rs.next())
	    {
		return TypeFactory.guessType(clazz).safeGet(rs, 1);
	    }
        } catch (SQLException ex) {
            throw new QueryException(ex);
        } finally {
	    JdbcUtils.close(rs);
            JdbcUtils.close(st);
            cm.releaseConnection(conn);
        }
	return null;
    }

    interface CallableStatementCallback {
        Object doInStatement(CallableStatement cs) throws SQLException;
    }


    interface PreparedStatementCallback {
        Object doInStatement(PreparedStatement st) throws SQLException;
    }
}
