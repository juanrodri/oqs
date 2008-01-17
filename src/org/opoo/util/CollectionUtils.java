/*
 * $Id$
 * Copyright 2002-2007 the original author or authors.
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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Miscellaneous collection utility methods.
 * Mainly for internal use within the framework.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 */
public abstract class CollectionUtils {
    private static final Log log = LogFactory.getLog(CollectionUtils.class);
    private static final boolean commonsCollections3Available =
	ClassUtils.isPresent(
		"org.apache.commons.collections.map.CaseInsensitiveMap",
		CollectionUtils.class.getClassLoader());


    /**
     * Return <code>true</code> if the supplied Collection is <code>null</code>
     * or empty. Otherwise, return <code>false</code>.
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection collection) {
	return (collection == null || collection.isEmpty());
    }

    /**
     * Return <code>true</code> if the supplied Map is <code>null</code>
     * or empty. Otherwise, return <code>false</code>.
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(Map map) {
	return (map == null || map.isEmpty());
    }

    public static void fill(Collection c, Object val, int size) {
	c.clear();
	int i = 0;
	while (i < size) {
	    c.add(val);
	    i++;
	}
    }

    public static Map createLinkedCaseInsensitiveMapIfPossible(int
	    initialCapacity) {
	if (commonsCollections3Available) {
	    log.trace("Creating [org.apache.commons.collections.map.ListOrderedMap/CaseInsensitiveMap]");
	    return CommonsCollectionFactory.createListOrderedCaseInsensitiveMap(
		    initialCapacity);
	} else {
	    log.debug(
		    "Falling back to [java.util.LinkedHashMap] for linked case-insensitive map");
	    return new LinkedHashMap(initialCapacity);
	}
    }


    private static abstract class CommonsCollectionFactory {
	private static Map createListOrderedCaseInsensitiveMap(int
		initialCapacity) {
	    // Commons Collections does not support initial capacity of 0.
	    return ListOrderedMap.decorate(new CaseInsensitiveMap(
		    initialCapacity ==
		    0 ? 1 : initialCapacity));
	}
    }
}
