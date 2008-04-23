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
import org.opoo.util.Assert;

/**
 *
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class SimpleExpression implements Criterion {
    private String qs;
    private Object value;
    private Type type;
    public SimpleExpression(String name, Object value, Type type, String op) {
	Assert.notNull(name, "criterion name can not be null");
	Assert.notNull(value, "criterion value can not be null");
	Assert.notNull(type, "criterion type can not be null");
        qs = name + op + "?";
        this.value = value;
        this.type = type;
    }

    public SimpleExpression(String name, Object value, String op) {
        this(name, value, value == null ? null : TypeFactory.guessType(value), op);
    }

    public Object[] getValues() {
        return new Object[] {value};
    }


    public String toString() {
        return qs;
    }

    public Type[] getTypes() {
        return new Type[] {type};
    }
}
