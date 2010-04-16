package org.apache.ws.scout.registry;

import java.util.Hashtable;
import org.uddi.api_v3.AuthToken;

public class AuthTokenV3Singleton {
	private static AuthTokenV3Singleton instance = new AuthTokenV3Singleton();
	private static Hashtable cachedAuthTokenHash = new Hashtable();
	
	private AuthTokenV3Singleton() {
		cachedAuthTokenHash = new Hashtable();
	}

	public static AuthToken getInstance(String username) {
		if (instance == null) {
			instance = new AuthTokenV3Singleton();
		}
		if (cachedAuthTokenHash.containsKey(username)) 
			return (AuthToken)cachedAuthTokenHash.get(username);
	
		return null;
	} 	
	
	public synchronized static void addAuthToken(String username, 
			AuthToken token) {
		if (instance == null) {
			instance = new AuthTokenV3Singleton();
		}
		cachedAuthTokenHash.put(username, token);
	}
	
	public synchronized static void deleteAuthToken(String username) {
		if (instance == null) {
			instance = new AuthTokenV3Singleton();
		} else {
			if (cachedAuthTokenHash.containsKey(username)) {
				cachedAuthTokenHash.remove(username);
			}
		}
	}
}