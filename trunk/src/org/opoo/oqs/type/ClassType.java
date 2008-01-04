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

/**
 * <tt>class</tt>: A type that maps an SQL VARCHAR to a Java Class.
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class ClassType extends ImmutableType {

    public Object valueOf(String value) {
        try {
            return classForName(value);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException("could not parse xml", cnfe);
        }
    }

    public Object get(ResultSet rs, String name) throws SQLException {
        String str = (String) Type.STRING.get(rs, name);
        if (str == null) {
            return null;
        } else {
            try {
                return classForName(str);
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("Class not found: " + str);
            }
        }
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        String str = (String) Type.STRING.get(rs, index);
        if (str == null) {
            return null;
        } else {
            try {
                return classForName(str);
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("Class not found: " + str);
            }
        }
    }

    private static Class classForName(String name) throws
            ClassNotFoundException {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().
                                             getContextClassLoader();
            if (contextClassLoader != null) {
                return contextClassLoader.loadClass(name);
            } else {
                return Class.forName(name);
            }
        } catch (Exception e) {
            return Class.forName(name);
        }
    }


    public Class getReturnedClass() {
        return Class.class;
    }


    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        //TODO: would be nice to handle proxy classes elegantly!
        Type.STRING.set(st, ((Class) value).getName(), index);
    }


    public int sqlType() {
        return Type.STRING.sqlType();
    }


    public String toString(Object value) {
        return ((Class) value).getName();
    }
}
