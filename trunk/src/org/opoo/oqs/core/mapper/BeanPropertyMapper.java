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
package org.opoo.oqs.core.mapper;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opoo.oqs.MapperException;
import org.opoo.oqs.core.Property;
import org.opoo.oqs.core.PropertyTypeAware;
import org.opoo.util.ClassUtils;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
public class BeanPropertyMapper extends AbstractMultiPropertyMapper {
    private static final Log log = LogFactory.getLog(BeanPropertyMapper.class);
    private final Class beanClass;
    public BeanPropertyMapper(String name, String string,
                              PropertyMapper[] mappers, String className) {
        super(name, string, mappers);
        this.beanClass = forName(className);
    }

    public BeanPropertyMapper(String name, String string,
                              PropertyMapper[] mappers, Class beanClass) {
        super(name, string, mappers);
        this.beanClass = beanClass;
    }

    public BeanPropertyMapper(Property sp, PropertyMapper[] mappers,
                              String className) {
        super(sp, mappers);
        this.beanClass = forName(className);
    }

    public BeanPropertyMapper(Property sp, PropertyMapper[] mappers,
                              Class beanClass) {
        super(sp, mappers);
        this.beanClass = beanClass;
    }

    /**
     *
     * @param rs ResultSet
     * @param rowNum int
     * @return Object
     * @throws SQLException
     */
    public Object map(ResultSet rs, int rowNum) throws SQLException {
        if (beanClass.isInterface()) {
            Map values = getValues(rs, rowNum, beanClass);
            return ClassUtils.createObject(values, beanClass);
        } else {
            //if is not interface
            Object target = newInstance(beanClass); //ClassUtils.newInstance(className);
            if (target == null) {
                log.debug("cannot instance: " + beanClass.getName());
                return null;
            }
            Map values = getValues(rs, rowNum, beanClass);
            populate(target, values);
            return target;
        }
        /*
         PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(target);
             for (int i = 0; i < mappers.length; i++)
             {
          PropertyMapper pm = mappers[i];
         if(PropertyTypeAware.class.isInstance(pm))//(pm instanceof PropertyTypeAware)
          {
         PropertyDescriptor pd = findPropertyDescriptor(pds, mappers[i].getName());
            if (pd != null)
            {
              ((PropertyTypeAware)pm).setPropertyType(pd.getPropertyType());
            }
          }

          Object value = mappers[i].mapRow(rs, rowNum);
          setProperty(target, mappers[i].getName(), value);
             }
             return target;
         */
    }

    /**
     * Map<String, ?> in JDK1.5
     *
     * @param rs ResultSet
     * @param rowNum int
     * @param type Class
     * @return Map
     * @throws SQLException
     */
    private Map getValues(ResultSet rs, int rowNum, Class type) throws
            SQLException {
        Map map = new HashMap();
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(type);
        for (int i = 0; i < mappers.length; i++) {
            PropertyMapper pm = mappers[i];
            if (PropertyTypeAware.class.isInstance(pm)) { //(pm instanceof PropertyTypeAware)
                PropertyDescriptor pd = findPropertyDescriptor(pds,
                        mappers[i].getName());
                if (pd != null) {
                    ((PropertyTypeAware) pm).setPropertyType(pd.getPropertyType());
                }
            }
            Object value = pm.map(rs, rowNum);
            map.put(pm.getName(), value);
            //setProperty(target, mappers[i].getName(), value);
        }
        return map;
    }

    private PropertyDescriptor findPropertyDescriptor(PropertyDescriptor[] pds,
            String name) {
        if (name == null) {
            return null;
        }
        for (int i = 0; i < pds.length; i++) {
            if (name.equals(pds[i].getName())) {
                return pds[i];
            }
        }
        return null;
    }

    /**
     * Object target, Map<String, ?> values)
     *
     * @param target Object
     * @param values Map
     */
    private void populate(Object target, Map values) {
        Iterator it = values.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Entry) it.next();
            if (entry.getValue() != null) {
                setProperty(target, (String) entry.getKey(), entry.getValue());
            }

        }
        /*
             for(Map.Entry entry: values.entrySet())
             {
               if(entry.getValue() != null)
               {
          setProperty(target, entry.getKey(), entry.getValue());
               }
             }*/
    }

    private void setProperty(Object target, String name, Object value) {
        try {
            PropertyUtils.setProperty(target, name, value);
        } catch (Exception ex) {
            log.error("set property error", ex);
        }
    }

    private Class forName(String className) {
        try {
            return ClassUtils.forName(className);
        } catch (ClassNotFoundException ex) {
            log.error(ex);
            throw new MapperException(ex);
        }
    }

    private Object newInstance(Class clazz) throws SQLException {
        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            log.error(ex);
            throw new MapperException(ex);
        }
    }

    public Class getReturnType() {
        return this.beanClass;
    }
}
