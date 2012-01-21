package com.mns.mojoinvest.client.header;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Header extends Composite {

    private static HeaderUiBinder uiBinder = GWT.create(HeaderUiBinder.class);

    interface HeaderUiBinder extends UiBinder<Widget, Header> {
    }

    public Header() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public Header(String firstName) {
        initWidget(uiBinder.createAndBindUi(this));
    }

}
