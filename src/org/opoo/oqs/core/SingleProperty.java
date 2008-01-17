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
package org.opoo.oqs.core;

import org.opoo.oqs.type.Type;
import org.opoo.oqs.type.TypeFactory;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class SingleProperty extends SimpleProperty implements PropertyTypeAware {
    private int index = -1;
    private Class type;
    public SingleProperty(String name, String string, int index, Class type) {
        super(name, string);
        this.index = index;
        this.type = type;
    }

    public SingleProperty(Property p, int index, Class type) {
        super(p.getName(), p.getString());
        this.index = index;
        this.type = type;
    }

    public SingleProperty(String name, String string, int index) {
        this(name, string, index, null);
    }

    public SingleProperty(Property p, int index) {
        this(p, index, null);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Class getPropertyType() {
        return type;
    }

    public Type getType() {
        if (type == null) {
            return null;
        }
        return TypeFactory.safeGuess(type);
    }

    public void setPropertyType(Class type) {
        this.type = type;
    }

    public String toString() {
        String s = super.toString();
        //System.out.println("Sigle:" + index + ", " + s);
        return s;
    }
}
