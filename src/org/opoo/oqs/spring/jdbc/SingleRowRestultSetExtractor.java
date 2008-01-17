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
package org.opoo.oqs.spring.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.opoo.oqs.jdbc.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;


/**
 * 实现ResultSetExtractor接口，处理ResultSet中只有一条记录的情况。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since 1.0
 */
public class SingleRowRestultSetExtractor implements ResultSetExtractor {
    private RowMapper rowMapper;

    /**
     *
     * @param rowMapper RowMapper
     */
    public SingleRowRestultSetExtractor(RowMapper rowMapper) {
        this.rowMapper = rowMapper;
    }

    /**
     * 实现ResultSetExtractor接口，处理ResultSet中只有一条记录的情况。
     * 当ResultSet中有多行时，忽略第2行之后的所有记录。
     *
     * @param rs ResultSet to extract data from. Implementations should not
     *   close this: it will be closed by the JdbcTemplate.
     * @return an arbitrary result object, or null if none (the extractor will
     *   typically be stateful in the latter case).
     * @throws SQLException if a SQLException is encountered getting column
     *   values or navigating (that is, there's no need to catch SQLException)
     * @throws DataAccessException
     */
    public Object extractData(ResultSet rs) throws SQLException,
            DataAccessException {
        if (rs.next()) {
            return rowMapper.mapRow(rs, 0);
        }
        return null;
    }
}
