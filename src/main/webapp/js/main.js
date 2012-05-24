goog.provide('example');
goog.require('app.params');
goog.require('goog.dom');

goog.require('goog.i18n.DateTimeFormat');
goog.require('goog.i18n.DateTimeParse');
goog.require('goog.ui.InputDatePicker');
goog.require('goog.ui.LabelInput');

example.sayHello = function (message) {
//    var data = {greeting:message, year:new Date().getFullYear()};
    var data = {};
    var html = app.params.options(data);
    goog.dom.getElement('hello').innerHTML = html;
};

example.main = function () {
    example.initRelativeStrengthRadio();
    example.initDatePickers();
}

example.initRelativeStrengthRadio = function () {
    // attach the click event handler to the radio buttons
    var radios = document.forms[0].elements["rsRadio"];
    for (var i = [0]; i < radios.length; i++)
        radios[i].onclick = example.rsRadioClicked;
};

example.rsRadioClicked = function () {
    // find out which radio button was clicked and
    // disable/enable appropriate input elements
    switch (this.value) {
        case "maRatioRadio" :
            goog.dom.getElement('ma1').disabled = false;
            goog.dom.getElement('ma2').disabled = false;
            goog.dom.getElement('roc').disabled = true;
            goog.dom.getElement('alpha').disabled = true;
            break;
        case "rocRadio" :
            goog.dom.getElement('ma1').disabled = true;
            goog.dom.getElement('ma2').disabled = true;
            goog.dom.getElement('roc').disabled = false;
            goog.dom.getElement('alpha').disabled = true;
            break;
        case "alphaRadio" :
            goog.dom.getElement('ma1').disabled = true;
            goog.dom.getElement('ma2').disabled = true;
            goog.dom.getElement('roc').disabled = true;
            goog.dom.getElement('alpha').disabled = false;
            break;
    }
};

example.initDatePickers = function () {
    var PATTERN = "MM'/'dd'/'yyyy";
    var formatter = new goog.i18n.DateTimeFormat(PATTERN);
    var parser = new goog.i18n.DateTimeParse(PATTERN);

    // Use a LabelInput for this one:
    var fieldLabelInput1 = new goog.ui.LabelInput('MM/DD/YYYY');
    fieldLabelInput1.render(goog.dom.getElement('fromDateContainer'));

    var idp1 = new goog.ui.InputDatePicker(formatter, parser);
    idp1.decorate(fieldLabelInput1.getElement());

    var fieldLabelInput2 = new goog.ui.LabelInput('MM/DD/YYYY');
    fieldLabelInput2.render(goog.dom.getElement('toDateContainer'));

    var idp2 = new goog.ui.InputDatePicker(formatter, parser);
    idp2.decorate(fieldLabelInput2.getElement());
};