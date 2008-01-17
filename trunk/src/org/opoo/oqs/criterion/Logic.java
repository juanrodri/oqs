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
package org.opoo.oqs.criterion;

import org.opoo.oqs.type.Type;
import org.opoo.util.ArrayUtils;
import org.opoo.util.Assert;

/**
 * Âß¼­±í´ïÊ½
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class Logic implements Criterion {
    private int criterionCount = 0;
    private StringBuffer qs = null;
    private Object[] values = null;
    private Type[] types = null;
    private Logic() {
        qs = new StringBuffer();
    }


    Logic(Criterion criterion) {
        this();
        addCriterion(criterion, "");
    }

    private void addCriterion(Criterion criterion, String op) {
        Assert.notNull(criterion, "criterion cannot be null");
        Object[] vals = criterion.getValues();
        Type[] tps = criterion.getTypes();

        if (vals != null && vals.length != 0 && tps != null && tps.length != 0) {
            if (values == null || types == null) {
                values = vals;
                types = tps;
            } else {
                values = ArrayUtils.concat(values, vals);
                types = (Type[]) ArrayUtils.concat(types, tps, Type.class);
            }
        }

        boolean moreThanOne = false;

        if (criterion instanceof Logic) {
            Logic res = (Logic) criterion;
            if (res.criterionCount > 1) {
                moreThanOne = true;
            }
            criterionCount += res.criterionCount;
        } else {
            criterionCount++;
        }
        if (moreThanOne) {
            qs.append(op + "(" + criterion.toString() + ")");
        } else {
            qs.append(op + criterion.toString());
        }
    }

    public int getCriterionCount() {
        return criterionCount;
    }

    public Logic and(Criterion criterion) {
        addCriterion(criterion, " AND ");
        return this;
    }

    public Logic or(Criterion criterion) {
        addCriterion(criterion, " OR ");
        return this;
    }

    public Object[] getValues() {
        if (values.length == 0) {
            return null;
        }
        return values;
    }

    public String toString() {
        if (qs.length() == 0) {
            return null;
        }
        return qs.toString();
    }

    public Type[] getTypes() {
        if (types.length == 0) {
            return null;
        }
        return types;
    }
}
