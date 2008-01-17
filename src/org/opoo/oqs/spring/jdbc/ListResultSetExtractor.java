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
import java.util.ArrayList;
import java.util.List;

import org.opoo.oqs.jdbc.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;


/**
 * {@link java.sql.ResultSet}数据提取器，返回{@link List}。
 * 将ResultSet中每一条记录使用指定的RowReader处理生成对象，然后置于List中。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since 1.0
 */
public class ListResultSetExtractor implements ResultSetExtractor {
    private RowMapper rowMapper;
    /**
     *
     * @param rowMapper RowMapper
     */
    public ListResultSetExtractor(RowMapper rowMapper) {
        this.rowMapper = rowMapper;
    }

    /**
     * ResultSet中每条记录对应List中一项。
     *
     * @param rs ResultSet
     * @return Object
     * @throws SQLException
     * @throws DataAccessException
     */
    public Object extractData(ResultSet rs) throws SQLException,
            DataAccessException {
        List list = new ArrayList();
        int i = 0;
        while (rs.next()) {
            Object result = rowMapper.mapRow(rs, i++); //.getResult(rs);
            //log.info(rowReader.getClass().getName() + ", " + result);
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }
}
