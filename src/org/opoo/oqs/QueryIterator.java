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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.opoo.oqs.jdbc.ConnectionManager;
import org.opoo.oqs.jdbc.JdbcUtils;

/**
 * Use this class must called close().
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class QueryIterator implements Iterator {
    private final ResultSet rs;
    private final PreparedStatement ps;
    private final ConnectionManager cm;
    private final Connection conn;
    private final Mapper mapper;
    protected boolean current = false;
    protected boolean eof = false;
    private int i = 0;

    public QueryIterator(ResultSet rs, PreparedStatement ps, Connection conn,
                         ConnectionManager cm, Mapper mapper) {
        this.rs = rs;
        this.ps = ps;
        this.cm = cm;
        this.conn = conn;
        this.mapper = mapper;
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements.
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        try {
            advance();
            return (!eof);
        } catch (SQLException e) {
            throw new RuntimeException("hasNext():  SQLException:  " + e);
        }
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    public Object next() {
        try {
            advance();
            if (eof) {
                throw new NoSuchElementException();
            }
            current = false;
            return mapper.map(rs, i++);
        } catch (SQLException e) {
            throw new RuntimeException("next():  SQLException:  " + e);
        }
    }

    /**
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).
     *
     */
    public void remove() {
        throw new UnsupportedOperationException
                ("FIXME - mapped operations not currently supported");
    }

    protected void advance() throws SQLException {
        if (!current && !eof) {
            if (rs.next()) {
                current = true;
                eof = false;
            } else {
                current = false;
                eof = true;
            }
        }
    }


    public void close() {
        JdbcUtils.close(rs);
        JdbcUtils.close(ps);
        cm.releaseConnection(conn);
    }
}
