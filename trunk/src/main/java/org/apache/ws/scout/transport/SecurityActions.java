/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.ws.scout.transport;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Security Privileged Actions
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a> 
 */
class SecurityActions 
{
	static String getProperty(final String key, final String defaultValue)
	{
		return AccessController.doPrivileged(new PrivilegedAction<String>()
	    {
			public String run() {
				return System.getProperty(key, defaultValue);
			} 
	    });
	}
	
	static String getProperty(final String key)
	{
		return AccessController.doPrivileged(new PrivilegedAction<String>()
	    {
			public String run() {
				return System.getProperty(key);
			} 
	    });
	}

}