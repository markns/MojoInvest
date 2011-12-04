package com.mns.mojoinvest.shared.model;

import java.io.Serializable;
import java.util.HashMap;

// transports http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/users/User.html info to client
public class UserInfo implements Serializable {

    public Boolean isSignedIn;
	public String userId;
	public String email;

	public HashMap<String, String> signInURLs = new HashMap<String, String>();
	public String signOutURL;
}
