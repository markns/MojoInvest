/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.mns.mojoinvest.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.mns.mojoinvest.client.resources.Resources;

/**
 * The top panel, which contains the 'welcome' message and various links.
 */
public class TopPanelView extends ViewImpl implements TopPanelPresenter.MyView {

    interface TopPanelBinder extends UiBinder<Widget, TopPanelView> {
    }

    private static final TopPanelBinder binder = GWT.create(TopPanelBinder.class);

    @UiField
    InlineHyperlink guide;

    @UiField
    InlineHyperlink app;
    @UiField
    InlineHyperlink landing;
    private final InlineHyperlink[] navigationLinks;

    private final Widget widget;
    private final Resources resources;

    @Inject
    public TopPanelView(final Resources resources) {
        this.resources = resources;
        this.resources.navigation().ensureInjected();
        this.widget = binder.createAndBindUi(this);
        this.navigationLinks = new InlineHyperlink[]{landing, app, guide};
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void highlight(String token) {
        if (token != null) {
            for (InlineHyperlink link : navigationLinks) {
                if (token.equals(link.getTargetHistoryToken())) {
                    link.addStyleName(resources.navigation().selectedNavigationEntry());
                } else {
                    link.removeStyleName(resources.navigation().selectedNavigationEntry());
                }
            }
        }
    }
}
