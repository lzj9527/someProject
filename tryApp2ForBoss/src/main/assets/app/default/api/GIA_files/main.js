;(function() {
    var $rates = $(".diamond-ratios");

    var Main = window.Main = _.inherit(Basic, {}, {
        diamondRate: {
            //count:0,
            //template: _.getTemplateById("new-rate-tpl"),
            $rates: $rates,
            init: function (validator,depends) {
                var self = this;
                this.validator = validator;
                this.depends = depends;
                this.bindEvents();
                this.validator.registerCallback("group_grater_than",function(value,matchName){
                    var el = this.form[matchName];
                    return value >= el.value;
                });
                this.validator.setMessage("group_grater_than","%s必须大于%s");
                this.$rates.on("blur",":text",function(){
                    var $this = $(this);
                    var $text = $this.closest("li").find(":text");
                    var index = $text.index($this);
                    //修复分组分数
                    if(index < 2){
                        self.fixDecimal($this);
                    }
                });
                this.$rates.find(":text").each(function(index){
                    self.addValidateField($(this),index);
                });
            },
            decimalLength:2,
            //自动补足小数位
            fixDecimal:function($input){
                var val = $.trim($input.val());
                if(val){
                    var split = val.split(".");
                    if(split.length <= 1){
                        val += ".0000".substr(0,this.decimalLength + 1);
                    } else if(split.length <= 2){
                        val = split[0] +  ("." + split[1] + "0000").substr(0, this.decimalLength + 1);
                    }
                    $input.val(val);
                }
            },
            length: $rates.children().length,
            bindEvents: function () {
                var diamondRate = this;
                $("#btn-new-dia-rate").on("click", function () {
                    diamondRate.add();
                });
                this.$rates.on("click", ".icon-close", function () {
                    var index = $(this).closest("li").index();
                    diamondRate.remove(index);
                });
            },
            remove: function (index) {
                var $li,self = this;
                if (this.length > 0) {
                    $li = this.$rates.children().eq(index);
                    $li.find(":text").each(function(){
                        self.removeValidateField(name);
                    });
                    $li.remove();
                }
            },
            addValidateField: function($el, index){
                var self = this;
                var $input = $el.closest("li").find(":text");
                var name = $el.attr("name");
                var display = $el.data("validate-display");
                var rules = "required|decimal";
                var isRate = index % 3 == 2;//校验倍率
                if(isRate){
                    rules += "|callback_decimal_max_length[3]|greater_than_or_equal[1]";
                } else {
                    rules += "|callback_decimal_max_length["+ this.decimalLength +"]|greater_than_or_equal[0.3]";
                }
                //验证结束分组
                if(index == 1){
                    rules += "|callback_group_grater_than[" + $input.eq(0).attr("name") + "]";
                }
                this.validator.addField({
                    name:name,
                    display: display,
                    rules: rules,
                    depends:function(field){
                        var isNotNull = function(){
                            var isNotNull = false;
                            var $elements = $(field.element).siblings(":text");
                            for(var i = 0; i < $elements.length; i++){
                                if($elements[i].value != ""){
                                    isNotNull = true;
                                    break;
                                }
                            }
                            return isNotNull;
                        };
                        return ( (typeof self.depends == "function") ? self.depends.call(this,field) : true ) && isNotNull();
                    }
                },name);
            },
            removeValidateField: function(name){
                this.validator.removeField(name);
            },
            add: function (data) {
                var self = this;
                var $li = $(_.renderTemplate("new-rate-tpl",{
                    index:this.length++
                }));
                this.$rates.append($li);
                $li.find(":text").each(function(index){
                    self.addValidateField($(this),index);
                });
            }
        }
    });

    var Validator = window.Validator = function (formName, rules, userAlert) {
        var form =(typeof formName === 'object') ? formName : document.getElementsByName(formName)[0];
        var $form = $(form);

        var validator = new FormValidator(formName, rules, function (errors, event) {
            this.handleErrors(errors, event);
        });

        validator.handleErrors = function(errors, event){
            var errorString, $value, $message;
            this.emptyErrorDom();
            //$form.find(".form-validate-error").hide().html("");

            if (errors.length > 0) {
                for (var i = 0, errorLength = errors.length; i < errorLength; i++) {
                    errorString = '';
                    for (var j = 0, messageLength = errors[i].messages.length; j < messageLength; j++) {
                        if (j != 0) {
                            errorString += "<br />";
                        }
                        errorString += errors[i].messages[j];
                    }
                    $value = $(errors[i].element).closest(".form-value");
                    if(userAlert){
                        top.Alert && top.Alert.show("提醒消息",errorString);
                    } else {
                        $message = $value.find(".form-validate-error");
                        if ($message[0]) {
                            $message.show();
                            //多个控件在同一栏时，只写入第一个错误控件的错误信息
                            if(!$message.html()){
                                $message.html(errorString);
                            }
                        } else {
                            $value.append("<div class='form-validate-error'>" + errorString + "</div>");
                        }
                    }
                }
                $(errors[0].element).focus();
            }
        };

        validator.emptyErrorDom = function(){
            $form.find(".form-validate-error").hide().html("");
        };

        validator.removeErrors = function(callback){
            FormValidator.prototype.removeErrors.call(this,callback);
            this.emptyErrorDom();
        };

        validator.setMessage("required", "您必须填写%s。");

        //验证平板名称
        validator.registerCallback("pad_name", function (value) {
            if (!/^[\u4E00-\u9FA5]{2,6}$/.test(value) && !/^[a-zA-Z\d_]{4,15}$/.test(value)) {
                return false;
            }
            return true;
        }).setMessage("pad_name", "%s为2-6个中文或4-15个英文、数字、下划线。");
		
		//验证下拉列表
        validator.registerCallback("select", function (value) {
            if (value === "请选择") {
                return false;
            }
            return true;
        }).setMessage("select", "请选择%s。");

        //验证平板品牌
        validator.registerCallback("pad_brand", function (value) {
            if (!/^[\u4E00-\u9FA5]{2,6}$/.test(value) && !/^[a-zA-Z]{2,15}$/.test(value)) {
                return false;
            }
            return true;
        }).setMessage("pad_brand", "%s为2-6个中文或2-15个英文。");

        //验证手机号码
        validator.registerCallback("mobile_no", function (value) {
            if (!/^\d{11}$/.test(value)) {
                return false;
            }
            return true;
        }).setMessage("mobile_no", "请输入11位有效手机号码。");

        //昵称
        validator.registerCallback("nick_name", function (value) {
            if (!/^[\u4E00-\u9FA5]{2,5}$/.test(value) && !/^[a-zA-Z\d_]{4,15}$/.test(value)) {
                return false;
            }
            return true;
        }).setMessage("nick_name", "%s为2-5个中文或4-15个英文、数字、下划线。");

        //验证真是姓名
        validator.registerCallback("real_name", function (value) {
            if (!/^[\u4E00-\u9FA5]{2,5}$/.test(value)) {
                return false;
            }
            return true;
        }).setMessage("real_name", "%s为2-5个中文字符。");

        validator.setMessage("min_length", "%s长度最小为%s个字符。");

        validator.setMessage("max_length", "%s长度最大为%s个字符。");

        validator.setMessage("matches", "%s必须与%s一致。");
		
		validator.setMessage("numeric", "%s只能包含数字。");

        validator.setMessage("valid_email","无效的邮箱地址。");

        validator.setMessage("decimal","%s必须为数字。");

        validator.setMessage("greater_than_or_equal","%s必须大于或等于%s。");

        validator.registerCallback("decimal_max_length", function (value,length) {
            var reg = new RegExp("^\\-?[0-9]*\\.?[0-9]{0," + length + "}$");
            if (!reg.test(value)) {
                return false;
            }
            return true;
        }).setMessage("decimal_max_length","%s最多包含%s位小数");

        return validator;
    };

    //ajax
    var loading =  window.top.Loading;
    var loadingTimeout;
    var showLoading = function(){
        clearLoadingTimeout();
        loadingTimeout = window.setTimeout(function(){
            loading && loading.show();
        },100);
    };
    var clearLoadingTimeout = function(){
        window.clearTimeout(loadingTimeout);
    };
    var hideLoading = function(){
        clearLoadingTimeout();
        loading && loading.hide();
    };

    $(document).ajaxStart(function(){
        showLoading();
    }).ajaxSend(function(){
        showLoading();
    }).ajaxComplete(function(){
        hideLoading();
    }).ajaxStop(function(){
        hideLoading();
    });
})();
