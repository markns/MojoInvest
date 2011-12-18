package com.mns.mojoinvest.shared.dispatch;


import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.In;
import com.gwtplatform.dispatch.annotation.Out;
import com.mns.mojoinvest.shared.model.UserInfo;

@GenDispatch(isSecure = false)
public class GetUser {

    @In(1)
	String requestURI;

	@Out(1)
	String errorText; // empty if success

    @Out(2)
    UserInfo userInfo;

//	@Out(3)
//	theaters user has access to; key/name
//	List<Theater> theaters = new ArrayList<Theater>();

}
