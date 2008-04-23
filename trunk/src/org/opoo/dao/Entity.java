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
package org.opoo.dao;

import java.io.Serializable;

/**
 * 所有实体（持久类）的超类，ID根据泛型可以取不同的数据类型。
 * 通常ID的类型是Long或者String。
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class Entity<K extends Serializable> implements Serializable {
    private K id;
    public Entity() {
    }

    public Entity(K id) {
        this.id = id;
    }

    public void setId(K k) {
        this.id = k;
    }

    public K getId() {
        return id;
    }
}
