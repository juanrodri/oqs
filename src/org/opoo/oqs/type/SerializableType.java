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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <tt>serializable</tt>: A type that maps an SQL VARBINARY to a
 * serializable Java object.
 *
 * @author Alex Lin
 * @version 1.0
 */
public class SerializableType extends MutableType {
    private final Class serializableClass;

    public SerializableType(Class serializableClass) {
        this.serializableClass = serializableClass;
    }


    private Object fromBytes(byte[] bytes) {
        return deserialize(bytes);
    }


    private Object deserialize(byte[] objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        return deserialize(bais);
    }


    private Object deserialize(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "The InputStream must not be null");
        }

        //log.trace("Starting deserialization of object");

        CustomObjectInputStream in = null;
        try {
            // stream closed in the finally
            in = new CustomObjectInputStream(inputStream);
            return in.readObject();

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("could not deserialize", ex);
        } catch (IOException ex) {
            throw new RuntimeException("could not deserialize", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {}
        }
    }


    private byte[] toBytes(Object value) {
        return serialize((Serializable) value);
    }


    private byte[] serialize(Serializable value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(value, baos);
        return baos.toByteArray();
    }


    private void serialize(Serializable obj, OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException(
                    "The OutputStream must not be null");
        }

        //if ( log.isTraceEnabled() ) {
        //   log.trace("Starting serialization of object [" + obj + "]");
        //}

        ObjectOutputStream out = null;
        try {
            // stream closed in the finally
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);

        } catch (IOException ex) {
            throw new RuntimeException("could not serialize", ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignored) {}
        }
    }


    public Object valueOf(String value) {
        return fromBytes((byte[]) Type.BINARY.valueOf(value));
    }


    public Object get(ResultSet rs, String name) throws SQLException {
        byte[] bytes = (byte[]) Type.BINARY.get(rs, name);
        if (bytes == null) {
            return null;
        } else {
            return fromBytes(bytes);
        }
    }

    public Object get(ResultSet rs, int index) throws SQLException {
        byte[] bytes = (byte[]) Type.BINARY.get(rs, index);
        if (bytes == null) {
            return null;
        } else {
            return fromBytes(bytes);
        }
    }

    public Class getReturnedClass() {
        return serializableClass;
    }


    public void set(PreparedStatement st, Object value, int index) throws
            SQLException {
        Type.BINARY.set(st, toBytes(value), index);
    }


    public int sqlType() {
        return Type.BINARY.sqlType();
    }


    public String toString(Object value) {
        return Type.BINARY.toString(toBytes(value));
    }

    /**
     * Custom ObjectInputStream implementation to more appropriately handle classloading
     * within app servers (mainly jboss - hence this class inspired by jboss's class of
     * the same purpose).
     */
    private static final class CustomObjectInputStream extends
            ObjectInputStream {

        public CustomObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        protected Class resolveClass(ObjectStreamClass v) throws IOException,
                ClassNotFoundException {
            String className = v.getName();
            Class resolvedClass = null;

            //log.trace("Attempting to locate class [" + className + "]");

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                resolvedClass = loader.loadClass(className);
                //log.trace("Class resolved through context class loader");
            } catch (ClassNotFoundException e) {
                //log.trace("Asking super to resolve");
                resolvedClass = super.resolveClass(v);
            }

            return resolvedClass;
        }
    }
}
