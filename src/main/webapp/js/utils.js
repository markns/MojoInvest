tpl = {

    // Recursively pre-load all the templates for the app.
    // This implementation should be changed in a production environment. All the template files should be
    // concatenated in a single file.
    loadTemplates:function (names, callback) {

        var loadTemplate = function (index) {
            var name = names[index];
            console.log('Loading template: ' + name);
            $.get('mustache/' + name + '.mustache', function (data) {
                ich.addTemplate(name, data);
                index++;
                if (index < names.length) {
                    loadTemplate(index);
                } else {
                    callback();
                }
            }, "html");
        };

        loadTemplate(0);
    },

    // Get template by name from hash of preloaded templates
    get:function (name) {
        return this.templates[name];
    }

};