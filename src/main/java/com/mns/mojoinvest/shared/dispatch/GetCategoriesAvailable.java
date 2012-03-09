package com.mns.mojoinvest.shared.dispatch;

import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.Out;

import java.util.HashSet;

@GenDispatch(isSecure = false)
public class GetCategoriesAvailable {

    @Out(1)
    String errorText; // empty if success

    @Out(2)
    HashSet<String> categoriesAvailable;

}
