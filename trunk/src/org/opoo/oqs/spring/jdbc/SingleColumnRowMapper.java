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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.opoo.oqs.jdbc.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;


/**
 * 处理{@link java.sql.ResultSet}中的一条记录,实现RowReader接口，处理ResultSet中每行只有一个字段的值的情况。
 *
 * 将此一个字段的值取出，直接返回。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since 1.0
 */
public class SingleColumnRowMapper implements RowMapper {


    public Object mapRow(ResultSet rs, int _int) throws SQLException {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int nrOfColumns = meta.getColumnCount();
            if (nrOfColumns != 1) {
                throw new DataAccessException(
                        "Expected single column but found " +
                        nrOfColumns);
            }

            return JdbcUtils.getResultSetValue(rs, 1);
        } catch (SQLException ex) {
        }
        return null;
    }
}
