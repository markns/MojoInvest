goog.provide('example');
goog.require('app');
goog.require('goog.dom');

example.sayHello = function (message) {
    var data = {greeting:message, year:new Date().getFullYear()};
    var html = app.welcome(data);
    goog.dom.getElement('hello').innerHTML = html;
};