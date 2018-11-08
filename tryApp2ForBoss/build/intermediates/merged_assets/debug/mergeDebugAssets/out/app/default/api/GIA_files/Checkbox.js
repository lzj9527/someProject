;(function(){
    var DEFAULTCLASSNAME = "icon";
    var UNCHECKEDCLASSNAME = "icon-checkbox";
    var CHECKEDCLASSNAME = "icon-checkbox-checked";

    var syncCallback = function($checkbox){
        var $checkboxs = this.getCheckboxs();
        var $all = this.getCheckAll();
        var $option = $checkboxs.not($all);
        var length,checkedLength;

        if($checkbox.data("role") == "all"){
            $all.prop("checked",$checkbox.prop("checked"));//多个全选按钮时，其他按钮也同步选中
            $option.not(":disabled").prop("checked",$checkbox.prop("checked"));
        } else if($all[0]){
            length = $option.length;
            checkedLength = $option.filter(function(){
                return this.checked;
            }).length;
            if(length > checkedLength){
                $all.prop("checked",false);
            } else {
                $all.prop("checked",true);
            }
        }
    };

    var Checkbox = _.inherit(Basic,{
        events:{
            onchange:syncCallback,
            oninit:syncCallback
        },
        onchange:null,
        //stopPropagation:false,
        //check:function(checked, untriggerEvent){
        //    this.$el.prop("checked",checked);
        //    this._sync();
        //    if(!untriggerEvent){
        //        this.triggerEvent("onchange",this.$el);
        //    }
        //},
        getLabel:function(){
            return this.$el.closest(".checkbox");
        },
        getName:function(){
            return this.$el.attr("name");
        },
        getChecked:function(){
            var name = this.getName();
            return $("input[type='checkbox'][name='" + name + "']:checked");
        },
        getCheckedExceptAll:function(){
            return this.getChecked().not("[data-roll='all']");
        },
        getCheckboxs:function(){
            var name = this.getName();
            return $("input[type='checkbox'][name='" + name + "']");
        },
        getCheckedValue:function(){
            var $checkbox = this.getCheckedExceptAll();
            var value = [];
            $checkbox.each(function(){
                value.push($(this).val());
            });
            return value;
        },
        getCheckAll:function(){
            var $checkboxs = this.getCheckboxs();
            return $checkboxs.filter(function(){
                return $(this).data("role") == "all";
            });
        },
        isChecked:function(){
            return this.$el.is(":checked");
        },
        _sync:function(){
            var $label = this.getLabel();
            var $icon = $label.find("." + DEFAULTCLASSNAME);

            $icon.attr("class",DEFAULTCLASSNAME + " " + UNCHECKEDCLASSNAME);
            if(this.el.checked){
                $icon.attr("class",DEFAULTCLASSNAME + " " + CHECKEDCLASSNAME);
            }
            if(this.el.disabled || this.el.readonly){
                $icon.attr("class",DEFAULTCLASSNAME + " " + UNCHECKEDCLASSNAME);
            }
        },
        init:function(){
            var self = this;
            this._sync();
            Checkbox.add(this.getName(),this);
            this.triggerEvent("oninit",this.$el);
            this.$el.on("change",function(e){
                self.triggerEvent("onchange",self.$el);
                Checkbox.syncCheckboxByName(self.getName());
            });
        }
    },{
        ins:{},
        init:function(name, options){
            var self = this;
            var $checkbox = $("input[type='checkbox'][name='" + name + "']");

            $checkbox.each(function(){
                var settings = $.extend({

                },options,{
                    $el:$(this)
                });
                new Checkbox(settings);
            });
            return Checkbox.ins[name];
        },
        has:function(name){
            return this.ins.hasOwnProperty(name);
        },
        add:function(name,ins){
            if(!this.has(name)){
                this.ins[name] = [];
            }
            this.ins[name].push(ins);
        },
        syncCheckboxByName:function(name){
            var ins = this.ins[name];
            _.each(ins,function(n,i){
                n._sync();
            });
        }
    });

    window.Checkbox = Checkbox;
    
})();
