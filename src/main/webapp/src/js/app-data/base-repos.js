define("base-repos", ["jquery", "json"], function(require, exports) {
  var
  /**
   * jquery module
   * @type {[type]}
   */
    $ = require("jquery"),
    /**
     * json module
     * @type {[type]}
     */
    jSon = require("json"),

    root = ((location.origin || (location.protocol + "//" + location.hostname + ":" + (location.port || "80"))) + "/ChinaDream");

  function convertParamToAjaxDataStr(params) {
    if (typeof params == "object") {
      for (var i in params) {
        dataJson += i + "=" + params[i] + "&";
      }
      dataJson = dataJson.substring(0, dataJson.length - 1);
      return dataJson;
    }
    return params;
  }

  function ajaxWrapper(type, url, params, ok, fail, error) {
    ajaxConfig = {
      url: url,
      type: type,
      data: params,
      dataType: "json",
      success: function(xmlHttp) {
        if (xmlHttp && xmlHttp.success) {
          var data = xmlHttp.data;
          if (typeof data == "string") {
            try{
              data = data.toJSON();
              if(typeof data == "string") {
                data = jSon.parse(data);
              }
            } catch(e){}
          }
          ok && ok(data);
        } else {
          fail && fail(xmlHttp);
        }
      },
      error: function(e) {
        error && error(e);
      }
    };

    type = type || "GET";
    if (type.toLowerCase() === "get") {
      url = url + "?r=" + new Date;
    }
    $.ajax(ajaxConfig);
  }

  function buildUrl(action) {
    return root + "/" + action + (action.indexOf(".action") > -1 ? "" : ".action");
  }

  function repoModule(action, isCache) {
    this.action = action;
    this.isCache = isCache;
  }

  repoModule.prototype = {
    get: function(params, ok, fail, error) {
      ajaxWrapper("GET", buildUrl(this.action), params, ok, fail, error);
    },
    post: function(params, ok, fail, error) {
      ajaxWrapper("POST", buildUrl(this.action), params, ok, fail, error);
    },
    submitForm: function(action, formId, ok, fail, error) {
      ajaxWrapper("POST", buildUrl(this.action), $("#" + formid).serialize(), ok, fail, error);
    }
  };
  /**
   * 设置action名称
   * @param {[type]} action [description]
   */
  exports.setAction = function(action, isCache) {
    return new repoModule(action, isCache);
  };
});