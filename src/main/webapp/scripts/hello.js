goog.provide('example');
goog.require('app.params');
goog.require('goog.dom');

example.sayHello = function (message) {
//    var data = {greeting:message, year:new Date().getFullYear()};
    var data = {};
    var html = app.params.options(data);
    goog.dom.getElement('hello').innerHTML = html;
};