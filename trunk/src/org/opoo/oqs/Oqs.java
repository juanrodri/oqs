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
 * @version 1.0
 */
public class Oqs {
    private static final Package PKG = Oqs.class.getPackage();
    private static final String OQS_INFO = new StringBuffer()
		    .append("***************************************************************")
		    .append("\n*  ").append(Oqs.getImplementationTitle()).append(" - ").append(Oqs.getDescription())
		    .append("\n*  Version : ").append(Oqs.getImplementationVersion())
		    .append("\n*  License : Apache License Version 2.0")
		    .append("\n*  Copyright 2006-2008 Alex Lin. All rights reserved.")
		    .append("\n***************************************************************")
		    .toString();

    public static String getImplementationVendor() {
        return (PKG != null ? PKG.getImplementationVendor() : null);
    }

    public static String getImplementationTitle() {
        return (PKG != null ? PKG.getImplementationTitle() : null);
    }

    public static String getImplementationVersion() {
        return (PKG != null ? PKG.getImplementationVersion() : null);
    }

    public static String getSpecificationVersion() {
        return (PKG != null ? PKG.getSpecificationVersion() : null);
    }

    public static String getSpecificationVendor() {
        return (PKG != null ? PKG.getSpecificationVendor() : null);
    }

    public static String getSpecificationTitle() {
        return (PKG != null ? PKG.getSpecificationTitle() : null);
    }

    public static String getDescription() {
	return "Simple O/R Mapping & JDBC Extensions";
    }

    public static String getOqsInfo() {
        return OQS_INFO;
    }
}
