package com.mns.mojoinvest.client.app.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.ui.client.ValueBoxEditorDecorator;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

import java.util.List;

public class ParamsView extends ViewWithUiHandlers<ParamsUiHandlers>
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
            new AbstractRenderer<Integer>() {
                @Override
                public String render(Integer integer) {
                    return integer.toString();
                }
            });

    @UiField
    Button runStrategyButton;

    @UiField
    ListBox providers;

    @UiField
    ListBox categories;

    public final Widget widget;

    public ParamsView() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPerformanceRangesAvailable(List<Integer> performanceRangeAcceptable) {
        pets.setValue(performanceRangeAcceptable.get(0));
        pets.setAcceptableValues(performanceRangeAcceptable);
    }

    @Override
    public void setProvidersAvailable(List<String> providersAvailable) {
        providers.clear();
        for (String provider : providersAvailable) {
            providers.addItem(provider);
        }
    }

    @Override
    public void setCategoriesAvailable(List<String> categoriesAvailable) {
        categories.clear();
        for (String category : categoriesAvailable) {
            categories.addItem(category);
        }
    }

    public void edit(Person person) {
        driver.initialize(this);
        driver.edit(person);
    }

    @UiHandler("runStrategyButton")
    void onSaveButtonClicked(ClickEvent event) {
        if (getUiHandlers() != null) {
            getUiHandlers().run();
        }
    }

    public Person flush() {
        return driver.flush();
    }


}
