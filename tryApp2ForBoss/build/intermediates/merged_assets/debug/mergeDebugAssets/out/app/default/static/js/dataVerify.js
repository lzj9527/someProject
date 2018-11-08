/**
 * 
 * 参数:
 *    options: {
 *      str: '' 需要做匹配的字符串
 *      rules: [
 *          // 匹配规则
 *      ] 
 *      success : function 匹配成功回调函数
 *      error: function 匹配不成功回掉函数
 *    }
 * 
 */
;(function (window, undefined) {
  
  // 所有检测规则
  var _verifyRules = {
    notEmpty: function (str) {
      var errorMsg = '请输入内容';
      return (str.trim() === '') ? errorMsg : true;
    },

    // GIA证书号码检测
    isGIANumber: function (str) {
      var reg = /^\d{10}$/, // 10位数字
        errorMsg = '请输入正确的GIA证书号码';
      return (reg.test(str)) ? true : errorMsg;
    }
  }

  var dataVerify = function (options) {
    var i, result, len, str;
    options = options || {};
    str = options.str;
    if(str === undefined){
      return;
    }
    str = str.trim();
    if (!options.rules) {
      return;
    }
    for (i = 0, len = options.rules.length; i < len; i++) {
      result = _verifyRules[options.rules[i]](options.str);
      if (result !== true) {
        options.error && options.error(result);
        return;
      }
    }
    options.success && options.success();
  }

  window.dataVerify = dataVerify;
})(window);



var strategies = {
      // 初始化数据
      init: function(str) {
        var result = '';
        result = this.isNonUndefined(str);
        result = result.trim();
        return result;
      },

      isNonUndefined: function (str, errorMsg) {
        if (str === void 0) {
          throw '被检验值是undefined';
        } else {
          return str;
        }
      },
      isNonEmpty: function (value, errorMsg) {
        if (value === '') {
          return errorMsg;
        }
      },
      minLength: function (value, length, errorMsg) {
        if (value.length < length) {
          return errorMsg;
        }
      },
      isMoblie: function (value, errorMsg) {
        if (!/(^1[3|4|5|7|8][0-9]{9}$)/.test(value)) {
          return errorMsg;
        }
      },
      fixedDigitsNumber: function (value, length, errorMsg) {
        var reg = new RegExp('^\\d{' + length + '}$'); //  "^\d{n}$"
        if(!reg.test(value)) {
          return errorMsg;
        }
      }

    };

    // var Validator = function () {
    //   this.cache = []; // 保存校验规则
    // }

    // Validator.prototype.add = function (str, rules) {
    //   var self = this;

    //   str = strategies.init(str);

    //   for (var i = 0, rule; rule = rules[i++];) {
    //     (function (rule) {
    //       var strategyAry = rule.strategy.split(':');
    //       var errorMsg = rule.errorMsg;
    //       self.cache.push(function () {
    //         var strategy = strategyAry.shift();
    //         strategyAry.unshift(str);
    //         strategyAry.push(errorMsg);
    //         return strategies[strategy].apply(strategies, strategyAry);
    //       });
    //     })(rule);
    //   }
    // };

    // Validator.prototype.start = function() {
    //   for (var i = 0, validatorFunc; validatorFunc = this.cache[i++];) {
    //     var errorMsg = validatorFunc();
    //     if (errorMsg) {
    //       return errorMsg;
    //     }
    //   }
    //   return true;
    // }