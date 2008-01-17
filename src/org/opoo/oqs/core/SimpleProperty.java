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

/**
 *
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class SimpleProperty implements Property {
    private final String name;
    private final String string;

    public SimpleProperty(String name, String string) {
        this.string = string;
        this.name = name;
    }

    public SimpleProperty(Property p) {
        this.name = p.getName();
        this.string = p.getString();
    }

    public String getString() {
        return string;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        if (name == null || name.equals(string)) {
            return string;
        }
        return string + " as " + name;
    }
}
