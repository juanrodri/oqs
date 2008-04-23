/*
 * $Id$
 *
 * Copyright 2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opoo.lang;

/**
 * The default numeric generator for generating long values. Implementation
 * allows for wrapping (to restart count) if the maximum is reached.
 *
 * @author Scott Battaglia
 */
public final class DefaultLongNumericGenerator implements LongNumericGenerator {

    /** The maximum length the string can be. */
    private static final int MAX_STRING_LENGTH = Long.toString(Long.MAX_VALUE)
        .length();

    /** The minimum length the String can be. */
    private static final int MIN_STRING_LENGTH = 1;

    /** The default wrap value of true. */
    private static final boolean DEFAULT_WRAP = true;

    /** The default initial value of 0. */
    private static final long DEFAULT_INTITIAL_VALUE = 0;

    /** Whether to wrap or not when we reach the maximum value. */
    private boolean wrap;

    /** The current number we are at. */
    private long count = 0;

    public DefaultLongNumericGenerator() {
        this.wrap = DEFAULT_WRAP;
    }

    public DefaultLongNumericGenerator(final boolean wrap) {
        this(DEFAULT_INTITIAL_VALUE, wrap);
    }

    public DefaultLongNumericGenerator(final long initialValue,
        final boolean wrap) {
        this.wrap = wrap;
        this.count = initialValue;
    }

    public DefaultLongNumericGenerator(final long initialValue) {
        this(initialValue, DEFAULT_WRAP);
    }

    public long getNextLong() {
        return this.getNextValue();
    }

    public String getNextNumberAsString() {
        return Long.toString(this.getNextValue());
    }

    public int maxLength() {
        return DefaultLongNumericGenerator.MAX_STRING_LENGTH;
    }

    public int minLength() {
        return DefaultLongNumericGenerator.MIN_STRING_LENGTH;
    }

    /**
     * @throws IllegalStateException if the maximum value is reached and
     * wrapping is not allowed.
     */
    protected synchronized long getNextValue() {
        if (!this.wrap && this.count == Long.MAX_VALUE) {
            throw new IllegalStateException(
                "Maximum value reached for this number generator.");
        }
        if (this.count == Long.MAX_VALUE) {
            this.count = 0;
        }

        return ++this.count;
    }
}
