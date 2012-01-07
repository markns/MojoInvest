package com.mns.mojoinvest.client.app.params;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class FundFilterEditor extends Composite {

    @UiField
    ListBox providers;

    @UiField
    ListBox categories;


    interface Binder extends UiBinder<Widget, FundFilterEditor> {
    }

    public FundFilterEditor() {
        initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
    }


}
