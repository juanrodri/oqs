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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.opoo.oqs.jdbc.DataAccessException;
import org.opoo.oqs.type.Type;
import org.springframework.jdbc.core.PreparedStatementSetter;


/**
 * 以绑定参数值和参数类型构建的PreparedStatementSetter。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since 1.0
 */
public class ArgTypePreparedStatementSetter implements PreparedStatementSetter {
    private final Object[] args;
    private final Type[] types;

    /**
     * 以值和类型构造PreparedStatementSetter。
     *
     * <p>Sample:
     * <pre>
     * PreparedStatementSetter pss = new ArgTypePreparedStatementSetter(values,
     * types);
     * </pre>
     *
     * @param args Object[] 参数的值数组
     * @param types Type[] 参数的类型数组
     */
    public ArgTypePreparedStatementSetter(Object[] args, Type[] types) {
        if ((args != null && types == null) || (args == null && types != null) ||
            (args != null && args.length != types.length)) {
            throw new DataAccessException(
                    "Values and Types parameters must match");
        }
        this.args = args;
        this.types = types;
        //System.out.println("Using PreparedStatementSetter: " + this);
    }

    /**
     * 设置PreparedStatement中绑定参数的值。
     * Set values on the given PreparedStatement
     *
     * @param ps PreparedStatement
     * @throws SQLException
     */
    public void setValues(PreparedStatement ps) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                types[i].safeSet(ps, args[i], i + 1);
            }
        }
    }
}
