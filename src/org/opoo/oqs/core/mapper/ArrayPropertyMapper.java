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

import org.opoo.oqs.core.Property;
import org.opoo.util.ArrayUtils;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class ArrayPropertyMapper extends AbstractMultiPropertyMapper {
    public ArrayPropertyMapper(String name, String string,
                               PropertyMapper[] mappers) {
        super(name, string, mappers);
    }

    public ArrayPropertyMapper(Property sp, PropertyMapper[] mappers) {
        super(sp, mappers);
    }

    public Object map(ResultSet rs, int rowNum) throws SQLException {
        Object[] objects = new Object[mappers.length];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = mappers[i].map(rs, rowNum);
        }
        return objects;
    }


    public Class getReturnType() {
        return ArrayUtils.EMPTY_OBJECT_ARRAY.getClass();
    }
}
