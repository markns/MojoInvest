package com.mns.mojoinvest.client.app.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.ui.client.ValueBoxEditorDecorator;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import java.io.IOException;
import java.util.List;

public class ParamsView extends ViewImpl
        implements ParamsPresenter.MyView, Editor<Person> {

    interface StrategyParamsViewUiBinder extends UiBinder<Widget, ParamsView> {
    }

    private static StrategyParamsViewUiBinder uiBinder = GWT.create(StrategyParamsViewUiBinder.class);

    interface Driver extends SimpleBeanEditorDriver<Person, ParamsView> {
    }

    private static Driver driver = GWT.create(Driver.class);

//    @UiField
//    HTMLPanel container;
//    @UiField
//    Button runStrategyButton;
//    @UiField
//    DateBox toDate;
//    @UiField
//    DateBox FromDate;
//    @UiField
//    TextBox volatilityFilter;
//    @UiField
//    TextBox portfolioSize;
//    @UiField
//    TextBox rebalanceFrequency;
//    @UiField
//    TextBox performanceRange;
//    @UiField
//    TextBox transactionCost;
//    @UiField
//    TextBox investmentAmount;
//    @UiField
//    TextBox categoryFilter;
//    @UiField
//    TextBox providerFilter;


    //--- Test
    @UiField
    AddressEditor address;

    @UiField
    ValueBoxEditorDecorator<String> description;

    @UiField
    ValueBoxEditorDecorator<String> name;

    @UiField
    ValueBoxEditorDecorator<String> note;

    @UiField
    Focusable nameBox;

    @UiField(provided = true)
    ValueListBox<Integer> pets = new ValueListBox<Integer>(
            new Renderer<Integer>() {
                @Override
                public String render(Integer integer) {
                    return integer.toString();
                }

                @Override
                public void render(Integer object, Appendable appendable)
                        throws IOException {
                    render(object);
                }
            });

    public final Widget widget;

    public ParamsView() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPerformanceRangeAcceptable(List<Integer> performanceRangeAcceptable) {
//        pets.
        pets.setAcceptableValues(performanceRangeAcceptable);

    }

    public void edit(Person person) {
        driver.initialize(this);
        driver.edit(person);
    }

    public Person flush() {
        return driver.flush();
    }


}
