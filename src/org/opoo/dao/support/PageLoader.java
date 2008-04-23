/*
 * $Id: PageLoader.java 50 2008-02-26 01:47:57Z alex@opoo.org $
 *
 * Copyright 2006-2008 Alex Lin. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.048 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.048
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opoo.dao.support;

import java.util.List;

/**
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public interface PageLoader {
    List find(ResultFilter f);
    int getCount(ResultFilter f);
}
