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
 * The project's version.
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 0.2
 */
public class OQSVersion {
    private static Package pkg = OQSVersion.class.getPackage();
    public static String getImplementationVendor() {
        return (pkg != null ? pkg.getImplementationVendor() : null);
    }

    public static String getImplementationTitle() {
        return (pkg != null ? pkg.getImplementationTitle() : null);
    }

    public static String getImplementationVersion() {
        return (pkg != null ? pkg.getImplementationVersion() : null);
    }
}
