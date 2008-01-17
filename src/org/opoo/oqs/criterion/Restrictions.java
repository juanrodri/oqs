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

import java.util.Collection;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class Restrictions { //implements Criterion
    public static SimpleExpression eq(String name, Object value) {
        return new SimpleExpression(name, value, "=");
    }

    public static SimpleExpression ne(String name, Object value) {
        return new SimpleExpression(name, value, "<>");
    }

    public static SimpleExpression ge(String name, Object value) {
        return new SimpleExpression(name, value, ">=");
    }

    public static SimpleExpression gt(String name, Object value) {
        return new SimpleExpression(name, value, ">");
    }

    public static SimpleExpression le(String name, Object value) {
        return new SimpleExpression(name, value, "<=");
    }

    public static SimpleExpression lt(String name, Object value) {
        return new SimpleExpression(name, value, "<");
    }

    public static Criterion in(String name, Object[] values) {
        return new In(name, values);
    }

    public static Criterion in(String name, Collection values) {
        return new In(name, values.toArray());
    }

    public static SimpleExpression like(String name, Object value) {
        return new SimpleExpression(name, value, " like ");
    }

    public static SimpleExpression ilike(String name, Object value) {
        return new SimpleExpression(name, value, " ilike ");
    }

    public static Criterion isNull(String name) {
        return new Null(name);
    }

    public static Criterion isNotNull(String name) {
        return new NotNull(name);
    }

    public static Criterion sql(String sql, Object[] values) {
        return new SqlCriterion(sql, values);
    }

    public static Logic logic(Criterion c) {
        return new Logic(c);
    }
}
