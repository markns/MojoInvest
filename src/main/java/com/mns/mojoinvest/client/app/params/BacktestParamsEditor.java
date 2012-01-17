package com.mns.mojoinvest.client.app.params;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.mns.mojoinvest.shared.params.BacktestParams;

public class BacktestParamsEditor extends Composite implements Editor<BacktestParams> {

    interface Binder extends UiBinder<Widget, BacktestParamsEditor> {
    }

    @UiField
    DateBox fromDate;
    @UiField
    DateBox toDate;

    public BacktestParamsEditor() {
        initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));

        DateTimeFormat dateFormat = DateTimeFormat.getFormat(
                DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
        fromDate.setFormat(new DateBox.DefaultFormat(dateFormat));
        toDate.setFormat(new DateBox.DefaultFormat(dateFormat));
    }
}
