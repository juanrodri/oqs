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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.opoo.oqs.core.Property;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.1
 */
public class DynaBeanPropertyMapper extends AbstractMultiPropertyMapper {
    public DynaBeanPropertyMapper(String name, String string,
                                  PropertyMapper[] mappers) {
        super(name, string, mappers);
    }

    public DynaBeanPropertyMapper(Property sp, PropertyMapper[] mappers) {
        super(sp, mappers);
    }

    /**
     *
     * @param resultSet ResultSet
     * @param _int int
     * @return Object
     * @throws SQLException
     */
    public Object map(ResultSet resultSet, int _int) throws SQLException {
        DynaBean bean = new LazyDynaBean();
        for (int i = 0; i < mappers.length; i++) {
            bean.set(mappers[i].getName(), mappers[i].map(resultSet, _int));
        }
        return bean;
    }

    public Class getReturnType() {
        return DynaBean.class;
    }
}
