// Generated by CoffeeScript 1.3.3
(function() {
  var Dictionary;

  Dictionary = Em.Namespace.create();

  (typeof exports !== "undefined" && exports !== null ? exports : this).Dictionary = Dictionary;

  Dictionary.ModelDefinition = Em.Object.extend(Util.Expandable, {
    _doExpand: function() {
      return Dictionary.ModelDefinition.findByName(this.get('name'), this);
    },
    published: (function() {
      var value;
      value = this.get('publishedDate');
      if (value) {
        return new Date(value).toDateString();
      }
    }).property('publishedDate')
  });

  Dictionary.ModelDefinition.reopenClass({
    findAll: function() {
      var modelDefinitions, result;
      modelDefinitions = [];
      result = $.Deferred();
      App.api.getModelDefinitions().done(function(data) {
        modelDefinitions.addObjects(data.map(function(item) {
          var modelDefinition;
          return modelDefinition = Dictionary.ModelDefinition.create(item);
        }));
        return result.resolve(modelDefinitions);
      });
      return result.promise(modelDefinitions);
    },
    findByName: function(name, modelDefinition) {
      var result;
      if (!modelDefinition) {
        modelDefinition = Dictionary.ModelDefinition.create({
          name: name
        });
      }
      modelDefinition.set('_expanded', true);
      result = $.Deferred();
      App.api.getModelMetadata(name).done(function(data) {
        data.types = data.types.map(function(item) {
          return Dictionary.ClassDefinition.create(item);
        });
        data.aspects = data.aspects.map(function(item) {
          return Dictionary.ClassDefinition.create(item);
        });
        modelDefinition.setProperties(data);
        return result.resolve(modelDefinition);
      });
      return result.promise(modelDefinition);
    }
  });

  Dictionary.ClassDefinition = Em.Object.extend(Util.Expandable, {
    _doExpand: function() {
      return Dictionary.ClassDefinition.findByName(this.get('name'), this);
    }
  });

  Dictionary.ClassDefinition.reopenClass({
    findByName: function(name, classDefinition) {
      var result;
      if (!classDefinition) {
        classDefinition = Dictionary.ClassDefinition.create({
          name: name
        });
      }
      classDefinition.set('_expanded', true);
      result = $.Deferred();
      App.api.getClassDefinition(name).done(function(data) {
        if (data.parent) {
          data.parent = Dictionary.ClassDefinition.create(data.parent);
        }
        if (data.children) {
          data.children = data.children.map(function(child) {
            return Dictionary.ClassDefinition.create(child);
          });
        }
        data.defaultAspects = data.defaultAspects.map(function(aspect) {
          return Dictionary.ClassDefinition.create(aspect);
        });
        data.model = Dictionary.ModelDefinition.create({
          name: data.model
        });
        data.associations.forEach(function(association) {
          association.source["class"] = Dictionary.ClassDefinition.create(association.source["class"]);
          return association.target["class"] = Dictionary.ClassDefinition.create(association.target["class"]);
        });
        if (data.properties) {
          data.properties.forEach(function(property) {
            if (property.containerClass) {
              return property.containerClass = Dictionary.ClassDefinition.create(property.containerClass);
            }
          });
        }
        classDefinition.setProperties(data);
        return result.resolve(classDefinition);
      });
      return result.promise(classDefinition);
    }
  });

  Dictionary.DataType = Em.Object.extend();

  Dictionary.DataType.reopenClass({
    findAll: function() {
      var dataTypes, result;
      result = $.Deferred();
      dataTypes = [];
      App.api.getDataTypes().done(function(data) {
        dataTypes.addObjects(data.map(function(item) {
          return Dictionary.DataType.create(item);
        }));
        return result.resolve(dataTypes);
      });
      return result.promise(dataTypes);
    }
  });

  Dictionary.Namespace = Em.Object.extend();

  Dictionary.Namespace.reopenClass({
    findAll: function() {
      var namespaces, result;
      result = $.Deferred();
      namespaces = [];
      App.api.getNamespaces().done(function(data) {
        data = data.filter(function(item) {
          return !Em.empty(item.uri);
        });
        data.map(function(item) {
          return Dictionary.Namespace.create(item);
        });
        namespaces.addObjects(data.map(function(item) {
          return Dictionary.Namespace.create(item);
        }));
        return result.resolve(namespaces);
      });
      return result.promise(namespaces);
    }
  });

}).call(this);