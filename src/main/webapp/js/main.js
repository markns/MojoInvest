Backbone.View.prototype.close = function () {
    console.log('Closing view ' + this);
    if (this.beforeClose) {
        this.beforeClose();
    }
    this.remove();
    this.unbind();
};


var AppRouter = Backbone.Router.extend({

    initialize:function (params) {
        this.model = new AppModel();
        this.view = new ParamsView({model:this.model});
        $('#params').html(new ParamsView().render().el);
    },

    routes:{
        "results":"results"
    },

    results:function () {
    }

});

tpl.loadTemplates(['params'], function () {
    window.app = new AppRouter();
    Backbone.history.start();
});


//example.main = function () {
//    example.initRelativeStrengthRadio();
//    example.initDatePickers();
//}
//
//example.initRelativeStrengthRadio = function () {
//    // attach the click event handler to the radio buttons
//    var radios = document.forms[0].elements["rsRadio"];
//    for (var i = [0]; i < radios.length; i++)
//        radios[i].onclick = example.rsRadioClicked;
//};
//
//example.rsRadioClicked = function () {
//    // find out which radio button was clicked and
//    // disable/enable appropriate input elements
//    switch (this.value) {
//        case "maRatioRadio" :
//            goog.dom.getElement('ma1').disabled = false;
//            goog.dom.getElement('ma2').disabled = false;
//            goog.dom.getElement('roc').disabled = true;
//            goog.dom.getElement('alpha').disabled = true;
//            break;
//        case "rocRadio" :
//            goog.dom.getElement('ma1').disabled = true;
//            goog.dom.getElement('ma2').disabled = true;
//            goog.dom.getElement('roc').disabled = false;
//            goog.dom.getElement('alpha').disabled = true;
//            break;
//        case "alphaRadio" :
//            goog.dom.getElement('ma1').disabled = true;
//            goog.dom.getElement('ma2').disabled = true;
//            goog.dom.getElement('roc').disabled = true;
//            goog.dom.getElement('alpha').disabled = false;
//            break;
//    }
//};
//
//example.initDatePickers = function () {
//    var PATTERN = "MM'/'dd'/'yyyy";
//    var formatter = new goog.i18n.DateTimeFormat(PATTERN);
//    var parser = new goog.i18n.DateTimeParse(PATTERN);
//
//    // Use a LabelInput for this one:
//    var fieldLabelInput1 = new goog.ui.LabelInput('MM/DD/YYYY');
//    fieldLabelInput1.render(goog.dom.getElement('fromDateContainer'));
//
//    var idp1 = new goog.ui.InputDatePicker(formatter, parser);
//    idp1.decorate(fieldLabelInput1.getElement());
//
//    var fieldLabelInput2 = new goog.ui.LabelInput('MM/DD/YYYY');
//    fieldLabelInput2.render(goog.dom.getElement('toDateContainer'));
//
//    var idp2 = new goog.ui.InputDatePicker(formatter, parser);
//    idp2.decorate(fieldLabelInput2.getElement());
//};