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
package org.opoo.util;

import java.io.UnsupportedEncodingException;

/**
 * 包含一些字符串处理基本函数的工具类。
 *
 * @author Alex Lin
 * @version 1.0
 */
public abstract class StringUtils{
    /**
     * 根据特定的charset取字符串长度，中文2字节计。
     * @param string String
     * @param charset String
     * @return int
     */
    public static int length(String string, String charset) {
        if (string == null)
            return 0;
        try {
            return string.getBytes(charset).length;
        } catch (UnsupportedEncodingException ex) {
            return string.length();
        }
    }

    /**
     * 字符串左截取。
     *
     * @param string String
     * @param charset String
     * @param len int
     * @return String
     */
    public static String left1(String string, String charset, int len) {
        if (string == null)
            return null;
        if (charset == null)
            return string.substring(0, len);

        if (length(string, charset) < len)
            return string;

        StringBuffer sb = new StringBuffer();
        String str1 = string.substring(0, len / 2);
        sb.append(str1);
        int rl = length(str1, charset);
        int index = str1.length();
        while (rl < len) {
            char c = string.charAt(index++);
            rl += length(string.valueOf(c), charset);
            if (rl > len)
                break;
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 字符串左截取。
     * @param string String
     * @param charset String
     * @param len int
     * @return String
     */
    public static String left2(String string, String charset, int len) {
        if (string == null)
            return null;
        if (charset == null)
            return string.substring(0, len);
        byte[] bytes;
        try {
            bytes = string.getBytes(charset);
        } catch (UnsupportedEncodingException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
            return string.substring(0, len);
        }

        int i = len - 1;
        for (; i >= 0; i--) {
            //找到倒数第一个非汉字字符的位置
            if (bytes[i] > 0)
                break;
            //System.out.println((int)bytes[i]);
        }
        if ((len - i) % 2 == 1)
            return new String(bytes, 0, len);
        else
            return new String(bytes, 0, len - 1);
    }

    /**
     * 计算一个字符串中某个字符出现的次数。
     *
     * @param string String
     * @param character char
     * @return int
     */
    public static int countUnquoted(String string, char character) {
        if ('\'' == character) {
            throw new IllegalArgumentException(
                    "Unquoted count of quotes is invalid");
        }
        if (string == null)
            return 0;
        // Impl note: takes advantage of the fact that an escpaed single quote
        // embedded within a quote-block can really be handled as two seperate
        // quote-blocks for the purposes of this method...
        int count = 0;
        int stringLength = string.length();
        boolean inQuote = false;
        for (int indx = 0; indx < stringLength; indx++) {
            char c = string.charAt(indx);
            if (inQuote) {
                if ('\'' == c) {
                    inQuote = false;
                }
            } else if ('\'' == c) {
                inQuote = true;
            } else if (c == character) {
                count++;
            }
        }
        return count;
    }

    /**
     * 去掉字符串最后一个"."后面的内容。
     *
     * @param qualifiedName String
     * @return String
     */
    public static String qualifier(String qualifiedName) {
        int loc = qualifiedName.lastIndexOf(".");
        return (loc < 0) ? "" : qualifiedName.substring(0, loc);
    }

    /**
     * 取字符串最后一个"."后面的内容。
     *
     * @param qualifiedName String
     * @return String
     */
    public static String unqualify(String qualifiedName) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }


    /**
     * 使用指定的seperator将字符串数组连接成字符串。
     *
     * @param seperator String
     * @param strings String[]
     * @return String
     */
    public static String join(String seperator, String[] strings) {
        int length = strings.length;
        if (length == 0)
            return "";
        if (length == 1)
            return strings[0];
        StringBuffer buf = new StringBuffer(length * strings[0].length())
                           .append(strings[0]);
        for (int i = 1; i < length; i++) {
            buf.append(seperator).append(strings[i]);
        }
        return buf.toString();
    }

    /**
     * 使用指定的个数(length)的指定字符串(fillString)，用分割符（separator）连接成字符串。
     * <pre>
     *  fillAndToString("aa", ", ", 3)
     *    ==> "aa, aa, aa"
     * </pre>
     *
     * @param fillString String
     * @param separator String
     * @param length int
     * @return String
     */
    public static String fillToString(String fillString, String separator,
                                      int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i > 0)
                sb.append(separator);
            sb.append(fillString);
        }
        return sb.toString();
    }

    /**
     *
     *
     * @param bytes byte[]
     * @return String
     * @see org.apache.commons.codec.binary.Hex#encodeHex()
     */
    public static String encodeHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < bytes.length; ++j) {
            int b = bytes[j] & 0xFF;
            if (b < 0x10) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    /**
     *
     * @param hex String
     * @return byte[]
     * @see org.apache.commons.codec.binary.Hex#decodeHex()
     */
    public static byte[] decodeHex(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }
}
