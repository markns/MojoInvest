package com.mns.mojoinvest.server;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Provider;
import com.mns.mojoinvest.shared.model.UserInfo;

public class UserInfoProvider implements Provider<UserInfo> {

	public UserInfo get() {
		UserInfo userInfo = new UserInfo();

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) {
			userInfo.isSignedIn = true;
			userInfo.email = user.getEmail();
			userInfo.userId = user.getUserId();
		} else {
			userInfo.isSignedIn = false;
		}

		return userInfo;
	}
}
