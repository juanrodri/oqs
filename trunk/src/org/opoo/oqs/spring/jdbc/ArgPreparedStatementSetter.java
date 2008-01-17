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

import org.opoo.oqs.type.Type;
import org.opoo.oqs.type.TypeFactory;
import org.springframework.jdbc.core.PreparedStatementSetter;


/**
 * 一个参数的PreparedStatementSetter。
 * 参数是绑定的参数的值数组。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since 1.0
 */
public class ArgPreparedStatementSetter implements PreparedStatementSetter {

    private final Object[] args;

    /**
     * 以参数值数组构建对象。
     *
     * <p>Sample:
     * <pre>
     * PreparedStatementSetter pss = new ArgPreparedStatementSetter(values);
     * </pre>
     *
     * @param values Object[] 绑定的参数的值，如果values == null，则不做任何处理。
     */
    public ArgPreparedStatementSetter(Object[] values) {
        args = values;
    }

    /**
     * 设置PreparedStatement中绑定的参数值。
     *
     * @param ps PreparedStatement
     * @throws SQLException
     */
    public void setValues(PreparedStatement ps) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                setParameter(ps, args[i], i + 1);
            }
        }
    }

    /**
     * 使用TypeFactory.guessType()获得值的类型，并调用Type.safeSet设置值。
     *
     * @param st PreparedStatement
     * @param value Object
     * @param index int
     * @throws SQLException
     */
    private void setParameter(final PreparedStatement st,
                              final Object value,
                              final int index) throws SQLException {
        //1.使用guessType()
        if (value == null) {
            setParameter(st, value, index, Type.SERIALIZABLE);
        } else {
            setParameter(st, value, index, TypeFactory.guessType(value));
        }
        //2.不使用guessType()
        //st.setObject(index, value);
    }

    /**
     * 调用Type.nullSafeSet设置PreparedStatement指定参数的值。
     *
     * @param st PreparedStatement
     * @param value Object
     * @param index int
     * @param type Type
     * @throws SQLException
     * @see Type#safeSet
     */
    private void setParameter(final PreparedStatement st,
                              final Object value,
                              final int index,
                              final Type type) throws SQLException {
        type.safeSet(st, value, index);
    }
}
