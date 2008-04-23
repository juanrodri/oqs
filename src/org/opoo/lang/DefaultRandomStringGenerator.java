/*
 * $Id: DefaultRandomStringGenerator.java 1.0 08-1-3 ÏÂÎç10:28 $
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

import java.security.SecureRandom;

/**
 * Implementation of the RandomStringGenerator that allows you to define the
 * length of the random part.
 *
 * @author Scott Battaglia
 * @author Alex Lin
 */
public final class DefaultRandomStringGenerator implements
        RandomStringGenerator {

    /** The array of printable characters to be used in our random string. */
    private static final char[] PRINTABLE_CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012345679"
            .toCharArray();

    /** The default maximum length. */
    private static final int DEFAULT_MAX_RANDOM_LENGTH = 35;

    /** An instance of secure random to ensure randomness is secure. */
    private SecureRandom randomizer = new SecureRandom();

    /** The maximum length the random string can be. */
    private final int maximumRandomLength;

    public DefaultRandomStringGenerator() {
        this.maximumRandomLength = DEFAULT_MAX_RANDOM_LENGTH;
    }

    public DefaultRandomStringGenerator(final int maxRandomLength) {
        this.maximumRandomLength = maxRandomLength;
    }

    public int getMinLength() {
        return this.maximumRandomLength;
    }

    public int getMaxLength() {
        return this.maximumRandomLength;
    }

    public String getNewString() {
        final byte[] random = new byte[this.maximumRandomLength];

        this.randomizer.nextBytes(random);

        return convertBytesToString(random);
    }

    public String getNewString(int maxRandomLength) {
        /*char[] output = new char[maxRandomLength];
               int n = PRINTABLE_CHARACTERS.length;
               for(int i = 0 ; i < maxRandomLength ; i++)
               {
          output[i] = PRINTABLE_CHARACTERS[randomizer.nextInt(n)];
               }
               return new String(output);
         */
        final byte[] random = new byte[maxRandomLength];
        this.randomizer.nextBytes(random);
        return convertBytesToString(random);
    }

    private String convertBytesToString(final byte[] random) {
        final char[] output = new char[random.length];
        for (int i = 0; i < random.length; i++) {
            final int index = Math.abs(random[i] % PRINTABLE_CHARACTERS.length);
            output[i] = PRINTABLE_CHARACTERS[index];
        }
        return new String(output);
    }


    /**
     *
     * @param maxRandomLength int
     * @param seeds char[]
     * @return String
     */
    public String getNewString(int maxRandomLength, final char[] seeds) {
        final byte[] random = new byte[maxRandomLength];
        randomizer.nextBytes(random);
        return convertBytesToString(random, seeds);
    }

    private static String convertBytesToString(final byte[] random,
                                               final char[] seeds) {
        final char[] output = new char[random.length];
        for (int i = 0; i < random.length; i++) {
            final int index = Math.abs(random[i] % seeds.length);
            output[i] = seeds[index];
        }
        return new String(output);
    }

    public static void main(String[] args) {
        DefaultRandomStringGenerator gen = new DefaultRandomStringGenerator();
        //System.out.println(gen.getNewString(64));
        long start = System.currentTimeMillis();
        gen.getNewString(35);
        System.out.println(System.currentTimeMillis() - start);
    }
}
