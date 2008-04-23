/*
 * $Id: PageUtils.java 56 2008-03-02 13:50:21Z alex@opoo.org $
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
package org.opoo.dao.support;

import java.util.List;

import org.opoo.util.Assert;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class PageUtils {

    public static PageableList findPageableList(PageLoader pageLoader, ResultFilter r){
	Assert.isTrue(r.isPageable(), "必须包含分页参数");
	List list = pageLoader.find(r);
	int count = pageLoader.getCount(r);
	return new PageableList(list, r.getFirstResult(), r.getMaxResults(), count);
    }
}
