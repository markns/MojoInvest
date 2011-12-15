package com.mns.mojoinvest.server.handler;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.shared.dispatch.GetUserAction;
import com.mns.mojoinvest.shared.dispatch.GetUserResult;
import com.mns.mojoinvest.shared.model.UserInfo;


public class GetUserHandler implements
        ActionHandler<GetUserAction, GetUserResult> {

    protected final Provider<UserInfo> userInfoProvider;

    @Inject
    public GetUserHandler(final Provider<UserInfo> userInfoProvider) {
        this.userInfoProvider = userInfoProvider;
    }

    @Override
    public GetUserResult execute(GetUserAction action, ExecutionContext context)
            throws ActionException {

        UserInfo userInfo = userInfoProvider.get();

        UserService userService = UserServiceFactory.getUserService();

        if (userInfo.isSignedIn) {
            userInfo.signOutURL = userService.createLogoutURL(action
                    .getRequestURI());
        } else {
            userInfo.signInURLs.put("Google", userService.createLoginURL(
                    action.getRequestURI(), null, "google.com/accounts/o8/id",
                    null));
            userInfo.signInURLs.put("Yahoo", userService.createLoginURL(
                    action.getRequestURI(), null, "yahoo.com", null));
        }

        return new GetUserResult("", userInfo);
    }

    @Override
    public Class<GetUserAction> getActionType() {
        return GetUserAction.class;
    }

    @Override
    public void undo(GetUserAction action,
                     GetUserResult result,
                     ExecutionContext context) throws ActionException {
        // by default not undoable
    }
}
