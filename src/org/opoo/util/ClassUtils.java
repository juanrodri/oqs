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
package org.opoo.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 一个反射机制基本函数工具类。
 *
 * @author Alex Lin
 * @version 1.0
 */
public abstract class ClassUtils {

    private static final Log log = LogFactory.getLog(ClassUtils.class);
    private static final Class[] NO_CLASSES = {};
    private static final Class[] OBJECT = new Class[] {Object.class};
    private static final Method OBJECT_EQUALS;
    private static final Class[] NO_PARAM = {};

    private static final Method OBJECT_HASHCODE;
    static {
        Method eq;
        Method hash;
        try {
            eq = Object.class.getMethod("equals", OBJECT);
            hash = Object.class.getMethod("hashCode", NO_PARAM);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not find Object.equals() or Object.hashCode()", e);
        }
        OBJECT_EQUALS = eq;
        OBJECT_HASHCODE = hash;
    }

    public static boolean overridesEquals(Class clazz) {
        Method equals;
        try {
            equals = clazz.getMethod("equals", OBJECT);
        } catch (NoSuchMethodException nsme) {
            return false; // its an interface so we can't really tell
							// anything...
        }
        return!OBJECT_EQUALS.equals(equals);
    }

    public static boolean overridesHashCode(Class clazz) {
        Method hashCode;
        try {
            hashCode = clazz.getMethod("hashCode", NO_PARAM);
        } catch (NoSuchMethodException nsme) {
            return false; // its an interface so we can't really tell
							// anything...
        }
        return!OBJECT_HASHCODE.equals(hashCode);
    }

    public static Class forName(String name) throws ClassNotFoundException {
        return forName(name, getDefaultClassLoader());
    }

    public static Class forName(String name, ClassLoader contextClassLoader) throws
            ClassNotFoundException {
        if (contextClassLoader != null) {
            return contextClassLoader.loadClass(name);
        } else {
            return Class.forName(name);
        }
    }

    public static boolean isPublic(Class clazz, Member member) {
        return Modifier.isPublic(member.getModifiers()) &&
                Modifier.isPublic(clazz.getModifiers());
    }

    public static Object getConstantValue(String name) {
        Class clazz;
        try {
            clazz = forName(StringUtils.qualifier(name));
        } catch (ClassNotFoundException cnfe) {
            return null;
        }
        try {
            return clazz.getField(StringUtils.unqualify(name)).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static Constructor getDefaultConstructor(Class clazz) throws
            SecurityException, NoSuchMethodException {
        if (isAbstractClass(clazz))
            return null;
        Constructor constructor = clazz.getDeclaredConstructor(NO_CLASSES);
        if (!isPublic(clazz, constructor)) {
            constructor.setAccessible(true);
        }
        return constructor;
    }

    public static boolean isAbstractClass(Class clazz) {
        int modifier = clazz.getModifiers();
        return Modifier.isAbstract(modifier) || Modifier.isInterface(modifier);
    }

    public static Method getMethod(Class clazz, Method method) {
        try {
            return clazz.getMethod(method.getName(), method.getParameterTypes());
        } catch (Exception e) {
            return null;
        }
    }

    public static Object newInstance(String classname) {
        try {
            return forName(classname).newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Object newInstance(Class type) {
        try {
            return type.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void populate(final Object obj, Map map) {
        // 1.简单但容易出问题
        // BeanUtils.populate(obj, map);
        // System.out.println(map.get("id").getClass());
        // org.apache.commons.beanutils.BeanUtils.populate(obj, map);

        // 2.效率比较低
        Set set = map.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String name = (String) e.getKey();
            setProperty(obj, name, e.getValue());
        }
    }

    public static Object getProperty(final Object entity, final String name) {
        try {
            return PropertyUtils.getProperty(entity, name);
        } catch (Exception ex) {
            log.error(ex);
        }
        return null;
    }

    public static Object[] getProperties(final Object entity,
                                         final String[] names) {
        Object[] a = new Object[names.length];
        for (int i = 0; i < a.length; i++) {
            a[i] = getProperty(entity, names[i]);
        }
        return a;
    }

    public static Object getProperties(final Object entity,
                                       final String[] names,
                                       final Class type) {
        Object array = Array.newInstance(type, names.length);
        for (int i = 0; i < names.length; i++) {
            Object value = getProperty(entity, names[i]);
            Array.set(array, i, value);
        }
        return array;
    }

    public static void setProperty(final Object entity, String name,
                                   Object value) {
        try {
            PropertyUtils.setProperty(entity, name, value);
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    public static String getPureName(Class clazz) {
        String name = clazz.getName();
        int i = name.lastIndexOf(".");
        if (i > 0)
            return name.substring(i + 1);
        else
            return name;
    }

    /**
     * 为了解决向低版本的兼容问题，JDK1.4-不支持Class.getSimpleName()
     *
     * @param clazz  Class
     * @return String
     */
    public static String getSimpleName(Class clazz) {
        try {
            Method m = clazz.getClass().getMethod("getSimpleName", new Class[]{});
            return (String) m.invoke(clazz, (Object[])null);
        } catch (Exception ex) {
            return getPureName(clazz);
        }
    }

    public static Class getPropertyType(Object bean, String name) {
        try {
            return PropertyUtils.getPropertyType(bean, name);
        } catch (NoSuchMethodException ex) {
            return null;
        } catch (InvocationTargetException ex) {
            return null;
        } catch (IllegalAccessException ex) {
            return null;
        }
    }

    public static void setProperty(Object target, PropertyDescriptor prop,
                                   Object value) throws
            Exception {

        Method setter = prop.getWriteMethod();
        if (setter == null) {
            return;
        }

        Class[] params = setter.getParameterTypes();

        // convert types for some popular ones
        if (value != null) {
            if (value instanceof java.util.Date) {
                if (params[0].getName().equals("java.sql.Date")) {
                    value = new java.sql.Date(((java.util.Date) value).getTime());
                } else
                if (params[0].getName().equals("java.sql.Time")) {
                    value = new java.sql.Time(((java.util.Date) value).getTime());
                } else
                if (params[0].getName().equals("java.sql.Timestamp")) {
                    value = new java.sql.Timestamp(((java.util.Date) value).
                            getTime());
                }
            }
        }
        // Don't call setter if the value object isn't the right type
        if (isCompatibleType(value, params[0])) {
            setter.invoke(target, new Object[] {value});
        } else {
            throw new Exception(
                    "Cannot set " + prop.getName() + ": incompatible types.");
        }
    }

    public static boolean isCompatibleType(Object value, Class type) {
        // Do object check first, then primitives
        if (value == null || type.isInstance(value)) {
            return true;
        } else if (
                type.equals(Integer.TYPE) && Integer.class.isInstance(value)) {
            return true;
        } else if (type.equals(Long.TYPE) && Long.class.isInstance(value)) {
            return true;
        } else if (
                type.equals(Double.TYPE) && Double.class.isInstance(value)) {
            return true;
        } else if (type.equals(Float.TYPE) && Float.class.isInstance(value)) {
            return true;
        } else if (type.equals(Short.TYPE) && Short.class.isInstance(value)) {
            return true;
        } else if (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) {
            return true;
        } else if (
                type.equals(Character.TYPE) && Character.class.isInstance(value)) {
            return true;
        } else if (
                type.equals(Boolean.TYPE) && Boolean.class.isInstance(value)) {
            return true;
        } else {
            return false;
        }
    }



    public static Object createObject(Map properties, Class[] interfaceClasses) {
        return Proxy.newProxyInstance(interfaceClasses[0].getClassLoader(),
                                      interfaceClasses,
                                      new BeanInvocationHandler(properties));
    }

    public static Object createObject(Map properties, Class interfaceClass) {
        return createObject(properties, new Class[] {interfaceClass});
    }

    private static class BeanInvocationHandler implements InvocationHandler {
        private final Map map;
        public BeanInvocationHandler(Map map) {
            this.map = map;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws
                Throwable {
            // System.out.println("Proxy: " + proxy.getClass());
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                return map.get(getPropertyName(methodName));
            }
            if (methodName.startsWith("is")) {
                Object value = map.get(getPropertyName(methodName));
                if (value != null && Boolean.class.isInstance(value)) {
                    return value;
                }
            } else if (methodName.startsWith("set")) {
                if (args.length == 1) {
                    map.put(getPropertyName(methodName), args[0]);
                }
                return null;
            } else if (methodName.equals("equals")) {
                return (proxy == args[0] ? Boolean.TRUE : Boolean.FALSE);
            } else if (methodName.equals("hashCode")) {
                return new Integer(map.hashCode());
            } else if (methodName.equals("toString")) {
                return map.toString();
            }

            // String mn = method.toGenericString();
            // System.out.println(method.getName());
            // return null;
            throw new UnsupportedOperationException(methodName);
        }

        protected static String getPropertyName(String methodName) {
            String name = methodName.substring(3);
            if (name == null || name.length() == 0) {
                return name;
            }
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }
    }


    public static boolean isPresent(String className) {
        return isPresent(className, getDefaultClassLoader());
    }

    /**
	 * Determine whether the {@link Class} identified by the supplied name is
	 * present and can be loaded. Will return <code>false</code> if either the
	 * class or one of its dependencies is not present or cannot be loaded.
	 *
	 * @param className
	 *            the name of the class to check
	 * @param classLoader
	 *            the class loader to use (may be <code>null</code>, which
	 *            indicates the default class loader)
	 * @return whether the specified class is present
	 */
    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        } catch (Throwable ex) {
            if (log.isDebugEnabled()) {
                log.debug("Class [" + className +
                          "] or one of its dependencies is not present: " + ex);
            }
            return false;
        }
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            log.debug(
                    "Cannot access thread context ClassLoader - falling back to system class loader",
                    ex);
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }


    public static class PackageClassLoader extends ClassLoader {
        private String[] imports;
        public PackageClassLoader(String imports) {
            super();
            setImports(new String[] {imports});
        }

        public PackageClassLoader(String[] imports) {
            super();
            setImports(imports);
        }

        public void setImports(String[] imports) {
            this.imports = imports;
            for (int i = 0; i < imports.length; i++) {
                if (imports[i].endsWith(".*")) {
                    this.imports[i] = imports[i].substring(0,
                            imports[i].length() - 1);
                }
            }
        }

        protected Class findClass(String name) throws ClassNotFoundException {
            if (name.indexOf(".") != -1)
                throw new ClassNotFoundException(name);

            for (int i = 0; i < imports.length; i++) {
                Class c;
                if (imports[i].endsWith("." + name)) {
                    c = this.forName(imports[i]);
                } else {
                    c = this.forName(imports[i] + name);
                }
                if (c != null) {
                    return c;
                }
            }
            throw new ClassNotFoundException(name);
        }

        private Class forName(String name) {
            try {
                return ClassUtils.forName(name);
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }
    }


    public static final ClassLoader createPackageClassLoader(String[] packages) {
        return new PackageClassLoader(packages);
    }
}
