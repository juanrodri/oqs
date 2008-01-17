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
import java.util.Arrays;

/**
 * 处理{@link java.sql.ResultSet}中的一条记录，返回{@link java.util.List}。
 *
 * <p>按照ResultSet中字段的顺序，将各字段的值取出并生成List返回。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since 1.0
 */
public class ListRowMapper extends SimpleArrayRowMapper {
    public Object mapRow(ResultSet rs, int _int) throws SQLException {
        Object[] a = (Object[])super.mapRow(rs, _int);
        if (a != null) {
            return Arrays.asList(a);
        }
        return null;
    }
}
