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

import java.util.Arrays;

import org.opoo.oqs.type.Type;
import org.opoo.oqs.type.TypeFactory;
import org.opoo.util.StringUtils;

public class In implements Criterion {
    private final String qs;
    private final Object[] values;
    private final Type[] types;

    public In(String name, Object[] values) {
        this(name, values, null);
    }

    public In(String name, Object[] values, Type type) {
        this.values = values;
        qs = name + " in (" + StringUtils.fillToString("?", ", ", values.length) +
             ")";
        if (type == null) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null) {
                    type = TypeFactory.guessType(values[i]);
                    break;
                }
            }
            if (type == null) {
                type = Type.SERIALIZABLE;
            }
        }
        types = new Type[values.length];
        Arrays.fill(types, type);
    }

    public Object[] getValues() {
        return values;
    }

    public String toString() {
        return qs;
    }

    public Type[] getTypes() {
        return types;
    }
}
