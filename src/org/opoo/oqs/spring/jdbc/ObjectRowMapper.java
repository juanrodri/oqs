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
package org.opoo.oqs.spring.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.opoo.util.ClassUtils;
import org.springframework.jdbc.core.ColumnMapRowMapper;

/**
 * 处理{@link java.sql.ResultSet}中的一条记录，返回一个指定class name的类的实例。
 *
 * <p>此对象应当为一Value Object，有相应的属性、数据类型以及set和get方法。
 * 至少ResultSet中的字段别名应当与属性名一致。若ResultSet中有而class中没有，或者class中
 * 有而ResultSet中没有对应字段，都不会出错。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @since 1.0
 */
public class ObjectRowMapper extends ColumnMapRowMapper {
    private String objectClassName;

    /**
     * 以指定的class name构造RowReader.
     * @param objectClassName String 欲返回的对象的class name.
     */
    public ObjectRowMapper(String objectClassName) {
        this.objectClassName = objectClassName;
    }


    public Object mapRow(ResultSet rs, int _int) throws SQLException {
        if (objectClassName == null) {
            return null;
        }

        Object obj = ClassUtils.newInstance(objectClassName);

        if (obj == null) {
            return null;
        }

        Map map = (Map)super.mapRow(rs, _int);

        if (map == null) {
            return null;
        }

        ClassUtils.populate(obj, map);

        return obj;

    }
}
