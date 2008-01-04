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
package org.opoo.oqs.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * <tt>locale</tt>: A type that maps an SQL VARCHAR to a Java Locale.
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class LocaleType extends ImmutableType implements LiteralType {


    public Object valueOf(String string) {
        if (string == null) {
            return null;
        } else {
            StringTokenizer tokens = new StringTokenizer(string, "_");
            String language = tokens.hasMoreTokens() ? tokens.nextToken() : "";
            String country = tokens.hasMoreTokens() ? tokens.nextToken() : "";
            // Need to account for allowable '_' within the variant
            String variant = "";
            String sep = "";
            while (tokens.hasMoreTokens()) {
                variant += sep + tokens.nextToken();
                sep = "_";
            }
            return new Locale(language, country, variant);
        }
    }


    public Object get(ResultSet rs, String name) throws SQLException {
        return valueOf((String) Type.STRING.get(rs, name));
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        return valueOf((String) Type.STRING.get(rs, index));
    }

    public Class getReturnedClass() {
        return Locale.class;
    }


    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        Type.STRING.set(st, value.toString(), index);
    }

    public int sqlType() {
        return Type.STRING.sqlType();
    }


    public String toString(Object value) {
        return value.toString();
    }

    public String toSQLString(Object value) throws Exception {
        return ((LiteralType) Type.STRING).toSQLString(value.toString());
    }
}
