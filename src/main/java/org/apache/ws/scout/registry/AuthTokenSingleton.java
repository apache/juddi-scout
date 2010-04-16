package org.apache.ws.scout.registry;

import java.util.Hashtable;
import org.apache.ws.scout.model.uddi.v2.AuthToken;

public class AuthTokenSingleton {
	private static AuthTokenSingleton instance = new AuthTokenSingleton();
	private static Hashtable cachedAuthTokenHash = new Hashtable();
	
	private AuthTokenSingleton() {
		cachedAuthTokenHash = new Hashtable();
	}

	public static AuthToken getToken(String username) {
		if (instance == null) {
			instance = new AuthTokenSingleton();
		}

		if (cachedAuthTokenHash.containsKey(username)) 
			return (AuthToken) cachedAuthTokenHash.get(username);
	
		return null;
	} 	
	
	public synchronized static void addAuthToken(String username, 
			AuthToken token) {
		if (instance == null) {
			instance = new AuthTokenSingleton();
		}
		cachedAuthTokenHash.put(username, token);
	}
	
	public synchronized static void deleteAuthToken(String username) {
		if (instance == null) {
			instance = new AuthTokenSingleton();
		} else {
			if (cachedAuthTokenHash.containsKey(username)) {
				cachedAuthTokenHash.remove(username);
			}
		}
	}
}