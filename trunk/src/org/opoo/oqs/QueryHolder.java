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
package org.opoo.oqs;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.opoo.oqs.type.Type;
import org.opoo.oqs.type.TypeFactory;

/**
 *
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class QueryHolder implements Serializable {
    private List types;
    private List values;
    private StringBuffer qs;

    public QueryHolder() {
        qs = new StringBuffer();
        types = new ArrayList();
        values = new ArrayList();
    }

    public QueryHolder(String sql) {
        this();
        append(sql);
    }

    public QueryHolder append(String sql) {
        qs.append(sql);
        return this;
    }

    public QueryHolder add(Object value, Type type) {
        types.add(type);
        values.add(value);
        return this;
    }

    public QueryHolder add(Object value) {
        types.add(TypeFactory.guessType(value));
        values.add(value);
        return this;
    }

    public QueryHolder add(Object[] values) {
        for (int i = 0; i < values.length; i++) {
            add(values[i]);
        }
        return this;
    }

    public QueryHolder addInt(int value) {
        return add(new Integer(value), Type.INTEGER);
    }

    public QueryHolder addLong(long value) {
        return add(new Long(value), Type.LONG);
    }

    public QueryHolder addBoolean(boolean b) {
        return add(new Boolean(b), Type.BOOLEAN);
    }

    public QueryHolder addString(String string) {
        return add(string, Type.STRING);
    }

    public QueryHolder addSerializable(Serializable s) {
        return add(s, Type.SERIALIZABLE);
    }

    public Type[] getTypes() {
        return (Type[]) types.toArray(new Type[types.size()]);
    }

    public Object[] getValues() {
        return values.toArray();
    }

    public String getSql() {
        return qs.toString();
    }

    public String toKeyString() {
        return qs.toString();
    }

    public String toString() {
        String toStringSql = getSql();
        try {
            int index = toStringSql.indexOf('?');
            for (int count = 0; index > -1; count++) {
                Object value = values.get(count);
                Type type = (Type) types.get(count);
                String val = null;
                switch (type.sqlType()) {
                case Types.INTEGER: // '\004'
                    val = "" + ((Integer) value).intValue();
                    break;

                case Types.BIGINT:
                    val = "" + ((Long) value).longValue();
                    break;

                case Types.VARCHAR: // '\f'
                    val = "\'" + (String) value + "\'";
                    break;

                case Types.BOOLEAN: // '\020'
                    val = "" + ((Boolean) value).booleanValue();
                    break;
                default:
                    val = "\'" + value + "\'";
                    break;
                }
                toStringSql = toStringSql.substring(0, index) + val +
                              (index != toStringSql.length() - 1 ?
                               toStringSql.substring(index + 1) : "");
                index = toStringSql.indexOf('?', index + val.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "QueryString{ sql=" + toStringSql + '}';
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof QueryHolder)) {
            return false;
        }
        if (this == object) {
            return true;
        } else {
            QueryHolder otherQs = (QueryHolder) object;
            return qs == null && otherQs.qs == null ||
                    qs != null && qs.toString().equals(otherQs.qs.toString())
                    && types.equals(otherQs.types) &&
                    values.equals(otherQs.values);
        }
    }

    public int hashCode() {
        int hashCode = 1;
        if (qs != null) {
            hashCode += qs.toString().hashCode();
        }
        hashCode = hashCode * 31 + types.hashCode();
        hashCode = hashCode * 31 + values.hashCode();
        return hashCode;
    }
}
