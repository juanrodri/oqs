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
package org.opoo.oqs;

/**
 * 创建<code>Query</code>出错时抛出此异常。这是一个Runtime exception.
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 * @see Query
 * @since OQS1.0
 */
public class CannotCreateQueryException extends RuntimeException {

    /**
     * Constructor for DataAccessException.
     * @param msg message
     */
    public CannotCreateQueryException(String msg) {
        super(msg);
    }

    /**
     * 创建Query出错时抛出此异常。
     *
     * @param message String
     * @param cause Throwable
     */
    public CannotCreateQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
