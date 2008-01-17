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
package org.opoo.oqs.criterion;

import org.opoo.oqs.type.Type;
import org.opoo.oqs.type.TypeFactory;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class SqlCriterion implements Criterion {
    private final String sql;
    private final Object[] values;
    private final Type[] types;
    public SqlCriterion(String sql, Object[] values) {
        this.sql = sql;
        this.values = values;
        types = new Type[values.length];
        for (int i = 0; i < values.length; i++) {
            types[i] = TypeFactory.guessType(values[i]);
        }
    }

    public SqlCriterion(String sql, Object[] values, Type[] types) {
        this.sql = sql;
        this.values = values;
        this.types = types;
    }


    public Object[] getValues() {
        return values;
    }

    public String toString() {
        return sql;
    }

    public Type[] getTypes() {
        return types;
    }
}
