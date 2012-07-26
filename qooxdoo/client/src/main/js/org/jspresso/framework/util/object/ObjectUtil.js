/**
 * Copyright (c) 2005-2012 Vincent Vandenschrick. All rights reserved.
 * <p>
 * This file is part of the Jspresso framework. Jspresso is free software: you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version. Jspresso is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with Jspresso. If not, see <http://www.gnu.org/licenses/>.
 */

qx.Class.define("org.jspresso.framework.util.object.ObjectUtil",
{
  statics :
  {
    
    /**
     * Transforms a Qooxdoo object graph into a simple untyped JS object graph
     * ready to be sent to server-side. Only public properties are handled.
     * @param {var} root the object graph containing Qooxdoo objects.
     * @return {var} the corresponding untyped JS Object graph.
     */
    untypeObjectGraph : function(root) {
      var untypedRoot = null;
      if(root != null) {
        if(root instanceof Array) {
          untypedRoot = new Array();
          for(var i = 0; i < root.length; i++) {
            untypedRoot[i] = org.jspresso.framework.util.object.ObjectUtil.untypeObjectGraph(root[i]);
          }
        } else if(root instanceof qx.core.Object) {
          untypedRoot = new Object();
          untypedRoot["class"] = root.classname;
          if(qx.Class.implementsInterface(root, qx.data.IListData)) {
            untypedRoot["array"] = org.jspresso.framework.util.object.ObjectUtil.untypeObjectGraph(root.toArray());
          } else {
            var clazz = root.constructor;
            var properties = qx.Class.getProperties(clazz);
            for(var i = 0; i < properties.length; i++) {
              var propertyName = properties[i];
              if(propertyName.charAt(0) != "_") {
                untypedRoot[propertyName] = org.jspresso.framework.util.object.ObjectUtil.untypeObjectGraph(root.get(propertyName));
              }
            }
          }
        } else if (root instanceof Object){
          if(root instanceof Date) {
            untypedRoot = root;
          } else {
            untypedRoot = new Object();
            for(var member in root) {
              untypedRoot[member] = org.jspresso.framework.util.object.ObjectUtil.untypeObjectGraph(root[member]);
            }
          }
        } else {
          untypedRoot = root;
        }
      }
      return untypedRoot;
    },
    
    /**
     * Transforms an untyped JS object graph into a Qooxdoo object graph.
     * All JS object members are considered public properties whenever the class
     * hint refers to a Qooxdoo object.
     * @param {var} root the object graph containing untyped JS Object.
     * @return {var} the corresponding object graph containing typed Qooxdoo objects.
     */
    typeObjectGraph : function(root) {
      return org.jspresso.framework.util.object.ObjectUtil._typeAndDedupObjectGraph(root, new org.jspresso.framework.util.remote.registry.BasicRemotePeerRegistry());
    },

    _typeAndDedupObjectGraph : function(root, registry) {
      var typedRoot = null;
      if(root != null) {
        if(root instanceof Array) {
          typedRoot = new Array();
          var l = root.length;
          for(var i = 0; i < l; i++) {
            typedRoot[i] = org.jspresso.framework.util.object.ObjectUtil._typeAndDedupObjectGraph(root[i], registry);
          }
        } else if(root instanceof Object) {
          var className = root["class"];
          if(className) {
            var typedClass = qx.Class.getByName(className);
            if(typedClass) {
              typedRoot = new typedClass();
              if(root["guid"] && typedRoot instanceof org.jspresso.framework.util.remote.RemotePeer) {
                if(registry.isRegistered(root["guid"])) {
                  typedRoot = registry.getRegistered(root["guid"]);
                  return typedRoot;
                } else {
                  typedRoot.setGuid(root["guid"]);
                  registry.register(typedRoot);
                }
              }
              var a = root["array"];
              if(a) {
                typedRoot.append(org.jspresso.framework.util.object.ObjectUtil._typeAndDedupObjectGraph(a, registry));
              } else {
                delete root["class"];
                for (var propertyName in root) {
                  typedRoot.set(propertyName, org.jspresso.framework.util.object.ObjectUtil._typeAndDedupObjectGraph(root[propertyName], registry));
                }
              }
            }
          } else if(root instanceof Date) {
            typedRoot = root;
          } else {
            typedRoot = new Object();
            for(var member in root) {
              typedRoot[member] = org.jspresso.framework.util.object.ObjectUtil._typeAndDedupObjectGraph(root[member], registry);
            }
          }
        } else if (typeof root === "string"){
          var iso8601regexp = "^([0-9]{4})-([0-9]{2})-([0-9]{2})" +
                  "T([0-9]{2}):([0-9]{2})(:([0-9]{2})(\.([0-9]+))?)?" +
                  "(Z|(([-+])([0-9]{2}):([0-9]{2})))?$";
          if(root.match(iso8601regexp)) {
            typedRoot = new Date(root);
          } else {
            typedRoot = root
          }
        } else {
          typedRoot = root;
        }
      }
      return typedRoot;
    }
  }
});
