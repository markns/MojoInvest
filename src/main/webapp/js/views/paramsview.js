window.ParamsView = Backbone.View.extend({

    tagName:"div",

    initialize:function () {
//        this.model.bind("change", this.render, this);
//        this.model.bind("destroy", this.close, this);
    },

    render:function (eventName) {
        $(this.el).html(ich.params(this.model));
        return this;
    }

});