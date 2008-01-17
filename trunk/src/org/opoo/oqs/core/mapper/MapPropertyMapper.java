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
import java.util.Map;

import org.opoo.oqs.core.Property;
import org.opoo.util.CollectionUtils;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class MapPropertyMapper extends AbstractMultiPropertyMapper {

    public MapPropertyMapper(String name, String string,
                             PropertyMapper[] mappers) {
        super(name, string, mappers);
    }

    public MapPropertyMapper(Property sp, PropertyMapper[] mappers) {
        super(sp, mappers);
    }

    public Object map(ResultSet rs, int rowNum) throws SQLException {
        Map map = createColumnMap(mappers.length);
        for (int i = 0; i < mappers.length; i++) {
            PropertyMapper m = mappers[i];
            map.put(m.getName(), m.map(rs, rowNum));
        }
        return map;
    }

    protected Map createColumnMap(int columnCount) {
        return CollectionUtils.createLinkedCaseInsensitiveMapIfPossible(
                columnCount);
    }

    public Class getReturnType() {
        return Map.class;
    }
}
